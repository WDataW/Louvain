    package com.github.wdataw.louvain.graph;

    import com.github.wdataw.louvain.Partition;
    import org.gephi.graph.api.GraphController;
    import org.gephi.graph.api.GraphModel;
    import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
    import org.gephi.project.api.ProjectController;
    import org.openide.util.Lookup;

    import java.awt.geom.Point2D;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.PrintWriter;
    import java.util.*;

    public class JSONExporter {

        // calculates nodes positions based on edge connections using FA2
        private static Point2D[] computePositions(Graph graph) {
            List<Node> nodes = graph.getNodes();
            int numberOfNodes = graph.getSize();
            if (numberOfNodes == 0) return new Point2D[0];// if graph has 0 nodes -> nothing to compute

            // Gephi boilerplate
            // project setup
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();

            // model setup
            GraphModel graphModel = Lookup.getDefault()
                    .lookup(GraphController.class)
                    .getGraphModel();

            // factory setup
            org.gephi.graph.api.Graph gephiGraph = graphModel.getUndirectedGraph();
            org.gephi.graph.api.GraphFactory factory = graphModel.factory();// used to construct edges and nodes

            // convert each node from Node to gephi.graph.api.Node
            Map<String, org.gephi.graph.api.Node> nodeMap = new HashMap<>();
            for (Node n : nodes) {
                String id = stringify(n.getNodeId());
                org.gephi.graph.api.Node gNode = factory.newNode(id);// create Gephi Node
                gephiGraph.addNode(gNode);
                nodeMap.put(id, gNode);
            }

            // convert each edge from Edge to org.gephi.api.Edge
            for (Edge e : graph.getEdges()) {
                String n1 = stringify(e.getEndpoints().getNode1().getNodeId());// endpoint 1
                String n2 = stringify(e.getEndpoints().getNode2().getNodeId());// endpoint 2
                gephiGraph.addEdge(factory.newEdge(nodeMap.get(n1), nodeMap.get(n2), false));
            }

            ForceAtlas2 fa2 = new ForceAtlas2(null);// the algorithm to compute node positions
            // initialization
            fa2.setGraphModel(graphModel);
            fa2.initAlgo();
            fa2.resetPropertiesValues();
            // options
            fa2.setBarnesHutOptimize(true);
            fa2.setBarnesHutTheta(1.5);
            fa2.setLinLogMode(true);
            fa2.setScalingRatio(8.0);
            fa2.setGravity(0.8);

            int iterations = Math.min(2000, numberOfNodes * 2);
            for (int i = 0; i < iterations; i++) {
                fa2.goAlgo();// for each iteration refines node positions.
                // the outcome is nodes positioned based on their edge connections, which helps visualize communities in community detection
            }
            fa2.endAlgo();

            // extract positions of each node from nodeMap
            Point2D[] positions = new Point2D[numberOfNodes];
            for (int i = 0; i < numberOfNodes; i++) {
                org.gephi.graph.api.Node gNode = nodeMap.get(stringify(nodes.get(i).getNodeId()));
                positions[i] = new java.awt.geom.Point2D.Double(gNode.x(), gNode.y());
            }
            return positions;
        }

        // converts an int to a String
        private static String stringify(int i){
            return String.valueOf(i);
        }

        // converts a graph and its communities to a JSON file
        // the JSON file is later fetched by the website to visualize the graph using vis-network
        public static void toJSON(Graph graph, Partition finalCommunities,String directory,int index){
            Point2D[] positions = computePositions(graph);// compute the positions of all graph nodes

            // for each invocation, the method exports 2 JSON files, one before community optimization and one after
            String[] filePaths = {String.format("visualization/public/%s/initialGraph%d.json",directory, index),
                                 String.format("visualization/public/%s/optimizedGraph%d.json",directory, index)};

            Partition[] communities = {new Partition(graph),finalCommunities};// initial and final states

            // used to flag diconnected nodes, to exclude them from the visualization
            // reasoning: isolated nodes don't contribute to community detection in any way, therefore are considered as noise
            // NOTE: they are only ignored in visualization, but accounted for in everything else
            Set<Integer> connectedIds = new HashSet<>();

            for(Edge e:graph.getEdges()){// for every edge
                int node1Id = e.getEndpoints().getNode1().getNodeId();// endpoint1 id
                int node2Id = e.getEndpoints().getNode2().getNodeId();// endpoint2 id
                if(node1Id!=node2Id){ // if a node exists as an edge endpoint and this edge isn't a self-loop, then the node is connected
                    connectedIds.add(node1Id);
                    connectedIds.add(node2Id);
                }
            }

            for(int i=0;i<2;i++){// generate two files one for each community partition
                File file = new File(filePaths[i]);
                file.getParentFile().mkdirs();// to account for when 'directory' doesn't exist
                PrintWriter jsonFile = null;
                try{
                    jsonFile = new PrintWriter(file);
                }catch (FileNotFoundException e){
                    System.out.println(e.getMessage());
                    System.exit(0);
                }

                jsonFile.println("{");// start of JSON file
                jsonFile.println("\"nodes\":[");// start of nodes array
                List<Node> nodes = graph.getNodes();
                for(Node n: nodes){// for each node
                    // create a JSON object as {"id":number, "label":number, "group":number, "x":number, "y":number, "disconnected":boolean}
                    int nodeId = n.getNodeId();
                    String item = String.format("{\"id\":%d,\"label\":\"%d\",\"group\":%d,\"x\":%f,\"y\":%f,\"disconnected\":%b}",nodeId, nodeId, communities[i].communityOf(n), positions[nodeId].getX(), positions[nodeId].getY(),!connectedIds.contains(nodeId));
                    if(!n.equals(nodes.getLast())) item+=",";// to seperate between array items
                    jsonFile.println(item);
                }
                jsonFile.println("],");// end of nodes array

                jsonFile.println("\"edges\":[");// start of edges array
                List<Edge> edges = graph.getEdges();
                for(Edge e: edges){// for each edge
                    // create a JSON object as {"from":number, "to":number}
                    int node1Id = e.getEndpoints().getNode1().getNodeId();// endpoint1 id
                    int node2Id = e.getEndpoints().getNode2().getNodeId();// endpoint2 id
                    String item = String.format("{\"from\":%d,\"to\":%d}", node1Id, node2Id);
                    if(!e.equals(edges.getLast())) item+=",";// to seperate between array items
                    jsonFile.println(item);
                }
                jsonFile.println("]");// end of edges array
                jsonFile.println("}");
                jsonFile.close();
                System.out.println(String.format("Exported: "+filePaths[i]));
            }
        }
    }

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
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class JSONExporter {
        private static Point2D[] computePositions(Graph graph) {
            List<Node> nodes = graph.getNodes();
            int n = graph.getSize();
            if (n == 0) return new Point2D[0];

            // Init Gephi
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            GraphModel graphModel = Lookup.getDefault()
                    .lookup(GraphController.class)
                    .getGraphModel();
            org.gephi.graph.api.Graph gephiGraph = graphModel.getUndirectedGraph();
            org.gephi.graph.api.GraphFactory factory = graphModel.factory();

            // Convert nodes
            Map<String, org.gephi.graph.api.Node> nodeMap = new HashMap<>();
            for (Node n2 : nodes) {
                String id = stringify(n2.getNodeId());
                org.gephi.graph.api.Node gNode = factory.newNode(id);
                gephiGraph.addNode(gNode);
                nodeMap.put(id, gNode);
            }

            // Convert edges
            for (Edge e : graph.getEdges()) {
                String n1 = stringify(e.getEndpoints().getNode1().getNodeId());
                String n2 = stringify(e.getEndpoints().getNode2().getNodeId());
                gephiGraph.addEdge(factory.newEdge(nodeMap.get(n1), nodeMap.get(n2), false));
            }


            ForceAtlas2 fa2 = new ForceAtlas2(null);
            fa2.setGraphModel(graphModel);
            fa2.initAlgo();
            fa2.resetPropertiesValues();
            fa2.setBarnesHutOptimize(true);
            fa2.setBarnesHutTheta(1.5);
            fa2.setLinLogMode(true);
            fa2.setScalingRatio(8.0);
            fa2.setGravity(0.8);

            int iterations = Math.min(1000, n * 2);
            for (int i = 0; i < iterations; i++) {
                fa2.goAlgo();
            }
            fa2.endAlgo();

            // Extract positions
            Point2D[] positions = new Point2D[n];
            for (int i = 0; i < n; i++) {
                org.gephi.graph.api.Node gNode = nodeMap.get(stringify(nodes.get(i).getNodeId()));
                positions[i] = new java.awt.geom.Point2D.Double(gNode.x(), gNode.y());
            }
            return positions;
        }
        private static String stringify(int i){
            return String.valueOf(i);
        }

        public static void toJSON(Graph graph, Partition communities, String filePath){
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            PrintWriter jsonFile = null;
            try{
                jsonFile = new PrintWriter(file);
            }catch (FileNotFoundException e){
                System.out.println(e.getMessage());
                System.exit(0);
            }

            jsonFile.println("{");// start of JSON file
            jsonFile.println("\"nodes\":[");// start of nodes array
            Point2D[] positions = computePositions(graph);
            List<Node> nodes = graph.getNodes();
            for(Node n: nodes){
                int nodeId = n.getNodeId();
                String item = String.format("{\"id\":%d,\"label\":\"%d\",\"group\":%d,\"x\":%f,\"y\":%f}",nodeId, nodeId,communities.communityOf(n), positions[nodeId].getX(), positions[nodeId].getY());
                if(!n.equals(nodes.getLast())) item+=",";
                jsonFile.println(item);
            }
            jsonFile.println("],");// end of nodes array

            jsonFile.println("\"edges\":[");// start of edges array
            List<Edge> edges = graph.getEdges();
            for(Edge e: edges){
                int node1Id = e.getEndpoints().getNode1().getNodeId();
                int node2Id = e.getEndpoints().getNode2().getNodeId();
                String item = String.format("{\"from\":%d,\"to\":%d}", node1Id, node2Id);
                if(!e.equals(edges.getLast())) item+=",";
                jsonFile.println(item);
            }
            jsonFile.println("]");// end of edges array
            jsonFile.println("}");
            jsonFile.close();
        }

    }

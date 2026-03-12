    package com.github.wdataw.louvain.graph;

    import com.github.wdataw.louvain.Partition;
    import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
    import org.jgrapht.alg.drawing.model.Box2D;
    import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
    import org.jgrapht.alg.drawing.model.Point2D;
    import org.jgrapht.graph.DefaultEdge;
    import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

    import java.io.FileNotFoundException;
    import java.io.PrintWriter;
    import java.util.List;

    public class JSONExporter {
        private static Point2D[] computePositions(Graph graph){
            org.jgrapht.Graph<String, DefaultEdge> JGraph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
            for (Node n: graph.getNodes()){
                JGraph.addVertex(stringify(n.getNodeId()));
            }
            for(Edge e: graph.getEdges()){
                String node1Id = stringify(e.getEndpoints().getNode1().getNodeId());
                String node2Id = stringify(e.getEndpoints().getNode2().getNodeId());
                JGraph.addEdge(node1Id,node2Id);
            }
            FRLayoutAlgorithm2D<String, DefaultEdge> layout = new FRLayoutAlgorithm2D<>(100); // bump to 500+
            MapLayoutModel2D<String> model = new MapLayoutModel2D<>(new Box2D(5000, 5000));
            layout.layout(JGraph, model);

            Point2D[] positions = new Point2D[graph.getNodes().size()];
            List<Node> nodes = graph.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                Point2D pos = model.get(stringify(nodes.get(i).getNodeId()));
                positions[i] = pos;
            }
            return positions;
        }
        private static String stringify(int i){
            return String.valueOf(i);
        }

        public static void toJSON(Graph graph, Partition communities, String destination){
            PrintWriter jsonFile = null;
            try{
                jsonFile = new PrintWriter(destination);
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

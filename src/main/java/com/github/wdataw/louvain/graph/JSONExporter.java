    package com.github.wdataw.louvain.graph;

    import com.github.wdataw.louvain.Partition;

    import java.io.FileNotFoundException;
    import java.io.PrintWriter;
    import java.util.List;

    public class JSONExporter {
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
            List<Node> nodes = graph.getNodes();
            for(Node n: nodes){
                String item = String.format(java.util.Locale.US,"{\"id\":%d,\"label\":\"%d\",\"group\":%d}",n.getNodeId(), n.getNodeId(),communities.communityOf(n));
                if(!n.equals(nodes.getLast())) item+=",";
                jsonFile.println(item);
            }
            jsonFile.println("],");// end of nodes array

            jsonFile.println("\"edges\":[");// start of edges array
            List<Edge> edges = graph.getEdges();
            for(Edge e: edges){
                int node1Id = e.getEndpoints().getNode1().getNodeId();
                int node2Id = e.getEndpoints().getNode2().getNodeId();
                String item = String.format(java.util.Locale.US,"{\"from\":%d,\"to\":%d}", node1Id, node2Id);
                if(!e.equals(edges.getLast())) item+=",";
                jsonFile.println(item);
            }
            jsonFile.println("]");// end of edges array
            jsonFile.println("}");
            jsonFile.close();
        }

    }

package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.*;

import java.util.*;


public class Louvain {
    public static List<Map<Integer,Set<Integer>>> louvain(Graph graph, String directory){
        List<Map<Integer,Set<Integer>>> dendogram = new ArrayList<>();

        boolean canImprove = true;
        int level = 0;// in each level the communities are optimized then the graph is aggregated
        while(canImprove){// keep iterating until modularity no longer increases
            canImprove = false;

            Partition initialCommunities = new Partition(graph);// initialize communities;
            double initialModularity = modularityOf(graph, initialCommunities);

            Partition optimizedCommunities = optimize(graph, initialCommunities);// optimize communities
            double optimizedModularity = modularityOf(graph, optimizedCommunities);

//            JSONExporter.toJSON(graph,optimizedCommunities, directory, level);// for visualization create 2 json files, before and after optimization

            dendogram.add(mapSuperNodetoNodes(graph,optimizedCommunities));// keep a map of the nodes that a super node consists of (in every level the nodes in communities are compressed into one super node, we need to keep a track of super node meanings)
            Graph aggregatedGraph = aggregate(graph, optimizedCommunities);// aggregate graph, i.e. create new graph with super nodes

            if(optimizedModularity > initialModularity) canImprove = true;// if not, then we weren't able to find any better partition therefore the algorithm stops
            System.out.println(initialModularity+" init");
            System.out.println(optimizedModularity+" fini");
            graph = aggregatedGraph;
            level++;
        }
        return dendogram;// for each level, maps the super nodes to the original nodes they're composed of (e.g. superNode1 => {node1, node2, node3})
    }

    public static double modularityOf(Graph graph, Partition communities) {
        double m = graph.getGraphWeight();
        double modularity = 0.0;

        double[] communityWeights = communities.getCommunityWeightSum();
        double[] communityDegrees = communities.getCommunityDegreeSum();

        for (int c = 0; c < communityWeights.length; c++) {
            double sigmaCHat = communityDegrees[c];
            if (sigmaCHat == 0)
                continue; // if a community has a degree of 0 then it is either isolated or empty, and therefore won't affect the modularity

            double sigmaC = communityWeights[c];
            double twoM = 2 * m;
            modularity += (1 / twoM) * (sigmaC - (sigmaCHat * sigmaCHat) / twoM);
        }
        return modularity;
    }
    public static Graph aggregate(Graph graph, Partition communities){
        List<Edge> edges = graph.getEdges();
        Graph aggregatedGraph = new Graph();

        int idCounter = 0;
        Map<Integer,Node> communityToSuperNode = new HashMap<>();
        for(int i=0;i<graph.getOrder();i++){
            if(communities.sizeOf(i)==0)continue;// if the community is not empty then it is converted to a super vertex
            Node superNode = new Node(idCounter++);
            aggregatedGraph.addNode(superNode);
            communityToSuperNode.put(i,superNode);

            double communityWeight = communities.weightOfCommunity(i);// internal weight is collapsed into a self-loop for the super vertex
            if(communityWeight > 0)aggregatedGraph.addEdge(new Edge(superNode,superNode, communityWeight));
        }

        Map<String,Edge> superConnections = new HashMap<>();
        for(Edge e: edges){
            Node node1 = e.getEndpoints().getNode1();
            int community1 = communities.communityOf(node1);

            Node node2 = e.getEndpoints().getNode2();
            int community2 = communities.communityOf(node2);

            if(community1 == community2)continue; // already converted into a self-loop, don't convert to edge between super nodes

            int a = Math.min(community1,community2);
            int b = Math.max(community1,community2);
            String connection = a+"C"+b;

            Edge edgeBetweenSuperNodes = superConnections.get(connection);
            if(edgeBetweenSuperNodes==null){
                Node superNode1 = communityToSuperNode.get(community1);
                Node superNode2 = communityToSuperNode.get(community2);
                edgeBetweenSuperNodes = new Edge(superNode1,superNode2, e.getEdgeWeight());
                aggregatedGraph.addEdge(edgeBetweenSuperNodes);
                superConnections.put(connection, edgeBetweenSuperNodes);
            }else{
                double newWeight = edgeBetweenSuperNodes.getEdgeWeight() + e.getEdgeWeight();
                edgeBetweenSuperNodes.setEdgeWeight(newWeight);
            }
        }
        aggregatedGraph.updateAdjList();
        aggregatedGraph.updateGraphWeight();
        return aggregatedGraph;
    }
    private static Map<Integer,Set<Integer>> mapSuperNodetoNodes(Graph graph, Partition communities){
        Map<Integer, Integer> communityToSuperNode = new HashMap<>();

        int superNodeIndex = 0;
        for(int i=0 ;i<communities.getNodeToCommunity().length;i++){
            if(communities.getCommunitySizes()[i]==0)continue;// empty communities don't result in a super node

            communityToSuperNode.put(i, superNodeIndex++);
//          reasoning: communities are aggregated into super nodes, but ids reset in the next level of aggregation, e.g. community at index 17 could become super node at index 0 if the previous 16 communities are empty
        }

        Map<Integer,Set<Integer>> map = new HashMap<>();
        for(Node n: graph.getNodes()){
            int community = communities.communityOf(n);
            int superNodeId = communityToSuperNode.get(community);
            Set<Integer> nodes = map.getOrDefault(superNodeId, new HashSet<Integer>());
            nodes.add(n.getNodeId());
            map.put(superNodeId, nodes);
        }
        return map;
    }

    public static Partition optimize(Graph graph, Partition communities) {
        boolean canimprove = true;
        while (canimprove) {
            canimprove = false;

            for (Node node : graph.getNodes()) {
                int originalCommunity = communities.communityOf(node);

                communities.removeNodeFromCommunity(node);

                double bestModularityGain = 0.0;
                int bestCommunity = originalCommunity;

                // collect neighbor communities
                Map<Integer, Double> neighborCommunities = new HashMap<>();

                for (Edge edge : graph.getAdjList().get(node.getNodeId())) {

                    Node node1 = edge.getEndpoints().getNode1();
                    Node node2 = edge.getEndpoints().getNode2();

                    Node neighbor = node1.equals(node) ? node2 : node1;
                    if (neighbor.equals(node)) continue; // skip self-loops
                    int neighborCommunity = communities.communityOf(neighbor);
                    double currentConnectionWeight = neighborCommunities.getOrDefault(neighborCommunity, 0.0);
                    neighborCommunities.put(neighborCommunity, currentConnectionWeight + edge.getEdgeWeight());
                }

                // try moving node to each neighbor community
                for (int candidateCommunity : neighborCommunities.keySet()) {
                    double connectionWeight = neighborCommunities.get(candidateCommunity);
                    double newModularityGain = communities.computeModularityGain(node, candidateCommunity, connectionWeight);

                    if (newModularityGain > bestModularityGain) {
                        bestModularityGain = newModularityGain;
                        bestCommunity = candidateCommunity;
                    }
                }
                // apply best move
                communities.moveNodeToCommunity(node, bestCommunity);

                if (bestCommunity != originalCommunity) {
                    canimprove = true;
                }
            }
        }

        return communities;
    }
}
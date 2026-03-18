package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.*;

import java.util.*;


public class Louvain {

    public static List<Map<Integer,Set<Integer>>> louvain(Graph graph, String directory){
        /* steps:
            1- optimize graph communities
            2- add optimized community mapping to the dendrogram
            3- aggregate graph using the optimized communities
            4- repeat steps 1-3 for the new aggregated graph
            5- terminate when the modularity stops increasing
        * */

        // dendrogram maps each super node in level i to the set of nodes in level i-1 that were aggregated to become that super node
        List<Map<Integer,Set<Integer>>> dendrogram = new ArrayList<>();

        boolean canImprove = true;
        int level = 0;// in each level the communities are optimized then the graph is aggregated
        while(canImprove){// keep iterating until modularity no longer increases
            canImprove = false;

            Partition initialCommunities = new Partition(graph);// initialize communities;
            double initialModularity = modularityOf(graph, initialCommunities);// compute modularity before community optimization

            // 1- optimize graph communities
            Partition optimizedCommunities = optimize(graph, initialCommunities);// optimize communities
            double optimizedModularity = modularityOf(graph, optimizedCommunities);// compute modularity after community optimization

            // for visualization create 2 JSON files, before and after optimization. create them once then use the JSONs as needed
            JSONExporter.toJSON(graph,optimizedCommunities, directory, level);

            // 2- add optimized community mapping to the dendrogram
            dendrogram.add(getSuperNodeToNodes(graph,optimizedCommunities));

            // 3- aggregate graph using the optimized communities
            Graph aggregatedGraph = aggregate(graph, optimizedCommunities);

            // if the optimization increased the modularity, we give the algorithm another iteration, else we stop (no point of iterating if modularity isn't going to increase)
            if(optimizedModularity > initialModularity) canImprove = true;// 5- terminate when the modularity stops increasing

            System.out.println(initialModularity+" init");
            System.out.println(optimizedModularity+" fini");
            graph = aggregatedGraph;
            level++;
        }
        return dendrogram;
    }

    // modularity measures edge density within communities vs what's expected by chance
    // modularity ranges from -1 to 1, where higher means stronger community structure
    public static double modularityOf(Graph graph, Partition communities) {

        // get the needed data to compute: Q = (1/2m) * Σ(c∈C) ( Σ_c - (Σ_ĉ)^2 / 2m )
        double m = graph.getWeight();
        double[] communityWeights = communities.getCommunityWeightSum();
        double[] communityDegrees = communities.getCommunityDegreeSum();

        double modularity = 0.0;
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

    // converts a graph with optimized communities to an aggregated graph
    public static Graph aggregate(Graph graph, Partition communities){
        /* steps:
            1- convert each non-empty community to a super node with a self-loop of the community weight
            2- convert all edges between any two communities to a single edge between the super nodes resulting from these communities
        */
        List<Edge> edges = graph.getEdges();
        Graph aggregatedGraph = new Graph();

        // 1- convert each non-empty community to a super node with a self-loop of the community weight
        Map<Integer,Node> communityToSuperNode = createSuperNodes(aggregatedGraph, communities);

        // 2- convert all edges between any two communities to a single edge between the super nodes resulting from these communities
        Map<String,Edge> superConnections = new HashMap<>();// keeps track of edges between super nodes
        for(Edge e: edges){
            Node node1 = e.getEndpoints().getNode1();
            int community1 = communities.communityOf(node1);// community of endpoint1
            Node node2 = e.getEndpoints().getNode2();
            int community2 = communities.communityOf(node2);// community of endpoint2

            // sharing the same community means this is an internal edge, already converted to a self-loop
            if(community1 == community2)continue;

            // all connections between the same two communities result in the same id
            String connectionId = getConnectionId(community1,community2);

            Edge edgeBetweenSuperNodes = superConnections.get(connectionId);
            if(edgeBetweenSuperNodes==null){// if the connection doesn't exist
                // create a new connection and set the weight equal to the edge weight
                Node superNode1 = communityToSuperNode.get(community1);// super node representing community1
                Node superNode2 = communityToSuperNode.get(community2);// super node representing community2
                edgeBetweenSuperNodes = new Edge(superNode1,superNode2, e.getEdgeWeight());
                aggregatedGraph.addEdge(edgeBetweenSuperNodes);// add edge to the aggregated graph
                superConnections.put(connectionId, edgeBetweenSuperNodes);// add edge to superConnections, so later we can add more weight to it
            }else{// if the connection exists
                // add the weights of connection edges together
                double newWeight = edgeBetweenSuperNodes.getEdgeWeight() + e.getEdgeWeight();
                edgeBetweenSuperNodes.setEdgeWeight(newWeight);
            }
        }
        // graph adjList and weight are updated once we're done with insertions
        aggregatedGraph.updateAdjList();
        aggregatedGraph.updateGraphWeight();
        return aggregatedGraph;
    }

    // groups nodes based on edge connections
    // nodes with strong connections are grouped together in a community
    public static Partition optimize(Graph graph, Partition communities) {
        /* steps:
            1- select a node.
            2- remove the node from its current community
            3- calculate modularity gain for each neighboring community (including the original one)
            4- select the community with the highest modularity gain
            5- insert the node into the selected community
            6- repeat steps 1-5 until nodes are always inserted back to their original communities (found the best partition)
        */
        boolean canimprove = true;
        while (canimprove) {// keep iterating until nodes aren't moving between communities anymore, meaning you already found the best partition
            canimprove = false;

            for (Node node : graph.getNodes()) {// 1- select a node
                int originalCommunity = communities.communityOf(node);

                communities.removeNodeFromCommunity(node);// 2- remove the nodes from its current community

                // collect neighbor communities and their connection weight (the weight of edges connecting the node to a neighbor community)
                Map<Integer, Double> neighborCommunities = getNeighborCommunities(node,graph,communities);

                double bestModularityGain = 0.0;
                int bestCommunity = originalCommunity;

                // 3- calculate modularity gain for each neighboring community
                // modularity gain = connectionWeight - (degreeOfNode * degreeOfCommunity) / (2 * graphWeight)
                for (int candidateCommunity : neighborCommunities.keySet()) {// for each neighbor community
                    double connectionWeight = neighborCommunities.get(candidateCommunity);
                    double newModularityGain = communities.computeModularityGain(node, candidateCommunity, connectionWeight);

                    // 4- select the community with the highest modularity gain
                    if (newModularityGain > bestModularityGain) {
                        bestModularityGain = newModularityGain;
                        bestCommunity = candidateCommunity;
                    }
                }
                // 5- insert the node into the selected community (the one with the highest modularit gain)
                communities.moveNodeToCommunity(node, bestCommunity);

                // if some node moved to a new community this means we still have a chance to increase modularity
                // else, we have found the best partition
                if (bestCommunity != originalCommunity) {
                    canimprove = true;
                }
            }
        }
        return communities;
    }

    // returns all the neighbor communities of a node
    // including the connection weight between the node and these communities
     private static Map<Integer, Double> getNeighborCommunities(Node node, Graph graph, Partition communities){
         Map<Integer, Double> neighborCommunities = new HashMap<>();
        for (Edge edge : graph.getAdjList().get(node.getNodeId())) {// for all the incident edges on the current node
            Node node1 = edge.getEndpoints().getNode1();// endpoint1
            Node node2 = edge.getEndpoints().getNode2();// endpoint1
            Node neighbor = node1.equals(node) ? node2 : node1;// each edge has two endpoints, one of them must be the node itself and the other is its neighbor

            if (neighbor.equals(node)) continue; // skip self-loops, to not consider the self-loop community as a neighbor community
            int neighborCommunity = communities.communityOf(neighbor);

            // keep track of connection weight between the current node and each neighboring community
            // if conncetion is already tracked we add edge weight to it, else: it equals edge weight
            double currentConnectionWeight = neighborCommunities.getOrDefault(neighborCommunity, 0.0);
            neighborCommunities.put(neighborCommunity, currentConnectionWeight + edge.getEdgeWeight());// neighborCommunity -> connectionWeight
        }
        return neighborCommunities;
    }

    // maps each community to a new super node id, start counting from 0
    private static Map<Integer, Integer> getCommunityToSuperNodeId(Partition communities){
        Map<Integer, Integer> communityToSuperNode = new HashMap<>();
        int superNodeIndex = 0;// start superNode ids from 0

        for(int i=0 ;i<communities.getNodeToCommunity().length;i++){// for each community
            if(communities.getCommunitySizes()[i]==0) continue;// empty communities don't result in a super node

            communityToSuperNode.put(i, superNodeIndex++);
            // reasoning: communities are aggregated into super nodes, but ids reset in the next level of aggregation
            // e.g. community at index 17 could become super node at index 0 if the previous 16 communities are empty
            // this prevents IndexOutOfBounds exception when accessing nodes in the next level of aggregation
        }
        return communityToSuperNode;
    }

    // treats each community as a super node and creates a map of community -> nodesWithinCommunity
    // this method provides one entry for the dendrogram, one is generated for each aggregation level
    private static Map<Integer,Set<Integer>> getSuperNodeToNodes(Graph graph, Partition communities){

        Map<Integer, Integer> communityToSuperNodeId = getCommunityToSuperNodeId(communities);

        int superNodeIndex = 0;// start superNode ids from 0
        for(int i=0 ;i<communities.getNodeToCommunity().length;i++){
            if(communities.getCommunitySizes()[i]==0)continue;// empty communities don't result in a super node

            communityToSuperNodeId.put(i, superNodeIndex++);
            // reasoning: communities are aggregated into super nodes, but ids reset in the next level of aggregation
            // e.g. community at index 17 could become super node at index 0 if the previous 16 communities are empty
        }

        Map<Integer,Set<Integer>> map = new HashMap<>();
        for(Node n: graph.getNodes()){// map each node to the super node of the next level of aggregation
            int community = communities.communityOf(n);
            int superNodeId = communityToSuperNodeId.get(community);

            // get the current set of nodes mapped to 'superNodeId'
            Set<Integer> nodes = map.getOrDefault(superNodeId, new HashSet<Integer>());

            // add node to them
            nodes.add(n.getNodeId());
            map.put(superNodeId, nodes);
        }
        return map;
    }

    // turns every non-empty community to a super node, and returns the map for community -> superNode
    private static Map<Integer,Node> createSuperNodes(Graph aggregatedGraph, Partition communities){
        Map<Integer,Node> communityToSuperNode = new HashMap<>();
        Map<Integer,Integer> communityToSuperNodeId = getCommunityToSuperNodeId(communities);

        // for each community that could be converted to a super node (non-empty communities)
        for(int c : communityToSuperNodeId.keySet()){
            int superNodeId = communityToSuperNodeId.get(c);// get the calculated id for the super node
            Node superNode = new Node(superNodeId);// create the super node
            aggregatedGraph.addNode(superNode);// add super node to graph
            communityToSuperNode.put(c,superNode);// map the community to the super node

            double communityWeight = communities.weightOfCommunity(c);
            // internal weight is collapsed into a self-loop for the super vertex
            if(communityWeight > 0)aggregatedGraph.addEdge(new Edge(superNode,superNode, communityWeight));
        }
        return communityToSuperNode;
    }

    // always returns the same id for a connection between two communities, regardless of their order
    private static String getConnectionId(int community1, int community2){
        int a = Math.min(community1,community2);
        int b = Math.max(community1,community2);
        return a+"C"+b;
    }
}

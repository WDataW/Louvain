package com.github.wdataw.louvain;



import java.util.List;
import java.util.Map;

public class Partition {
    private int[] nodeToCommunity;
    private double[] nodeToDegree;
    private double[] communityWeightSum;// Sigma C
    private double[] communityDegreeSum;// Sigma C hat
    private Map<Integer, List<Edge>> adjList;// adjacency list for moveNodeToCommunity

    Partition(Graph graph){
        this.nodeToCommunity = initCommunities(graph);
        this.nodeToDegree = initDegrees(graph);
//      this.communityWeightSum = initCommunityWeights(graph);
        this.communityDegreeSum = initCommunityDegrees(graph);
    }


    private double[] initCommunityWeights(Graph graph){ // Angel
        // Array sized by number of nodes (community IDs match node IDs at initialization)
        double[] communityWeightSum = new double[graph.getNodes().size()];

        // At initialization each node is its own community, so NO edge can have
        // both endpoints in the same community (unless it's a self-loop).
        // We still iterate all edges to correctly handle self-loops.
        for(Edge e : graph.getEdges()){
            Node node1 = e.getEndpoints().getNode1();
            Node node2 = e.getEndpoints().getNode2();
            // An edge counts toward Sigma_C only when both endpoints share the same community
            if(nodeToCommunity[node1.getNodeId()] == nodeToCommunity[node2.getNodeId()]){
                int community = nodeToCommunity[node1.getNodeId()];
                if(node1.equals(node2)){
                    communityWeightSum[community] += e.getEdgeWeight();// self-loop: count once
                } else {
                    communityWeightSum[community] += 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
                }
            }
        }
        return communityWeightSum;
    }

    
    /*this method is for calculating the sigma C hat 
    when every node in its community*/
    private double[] initCommunityDegrees(Graph graph){ 
        
        /*
        initilize the communityDegreeSum array
        with the size of the number communities
        */
        double[] communityDegreeSum = new double[nodeToCommunity.length];
        
        // for each node in the graph
        for(Node node: graph.getNodes()){
            //store the node id
            int nodeId = node.getNodeId();
            //store the degree of that node from the nodeToDegree array
            double degree = degreeOfNode(nodeId);
            //store the degree of the node in the communityDegreeSum array
            communityDegreeSum[nodeId] = degree;
        }
        // reasoning: at the beginning each node is in its own community, therefore the degree of each community = the degree of the node within it
        //return the array
        return communityDegreeSum;
    }

    
    // moves a node from its current community to the specified newCommunity
    public void moveNodeToCommunity(Node node, int newCommunity){
        int oldCommunity = nodeToCommunity[node.getNodeId()];
        nodeToCommunity[node.getNodeId()] = newCommunity;

        // update communityDegreeSum for both the involved communities - Tamim

        // update communityWeightSum for both the involved communities - Angel
        // For every neighbor of `node`, check if the edge weight needs to be
        // moved from oldCommunity's sum to newCommunity's sum (or removed/added).
        for(Edge e : adjList.get(node.getNodeId())){
            Node neighbor;
            if(e.getEndpoints().getNode1().equals(node)){
                neighbor = e.getEndpoints().getNode2();
            } else {
                neighbor = e.getEndpoints().getNode1();
            }

            // self-loop: neighbor is the node itself, use oldCommunity since we already updated nodeToCommunity
            boolean isSelfLoop = neighbor.equals(node);
            int neighborCommunity;
            if(isSelfLoop){
                neighborCommunity = oldCommunity;
            } else {
                neighborCommunity = nodeToCommunity[neighbor.getNodeId()];
            }

            // The neighbor was previously with `node` in oldCommunity
            // → that edge no longer counts toward oldCommunity's weight
            if(neighborCommunity == oldCommunity){
                if(isSelfLoop){
                    communityWeightSum[oldCommunity] -= e.getEdgeWeight();// self-loop: count once
                } else {
                    communityWeightSum[oldCommunity] -= 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
                }
            }
            // The neighbor is in newCommunity (or this is a self-loop moving to newCommunity)
            // → that edge now counts toward newCommunity's weight
            if(neighborCommunity == newCommunity || isSelfLoop){
                if(isSelfLoop){
                    communityWeightSum[newCommunity] += e.getEdgeWeight();// self-loop: count once
                } else {
                    communityWeightSum[newCommunity] += 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
                }
            }
            
        }
        
    }

    // NOTE: only invoke before actually moving the node to a new community
    // used to always ensure the total degree is reserved, e.g. +1 degree in one community means -1 in another.
    private void updateCommunityDegree(int nodeId,int newCommunity){
        int oldCommunity = communityOf(nodeId);
        double nodeDegree = degreeOfNode(nodeId);
        communityDegreeSum[newCommunity] += nodeDegree;
        communityDegreeSum[oldCommunity] -= nodeDegree;
    }

    private int[] initCommunities(Graph graph){// initializes the communities, each node = a community
        int[] nodeToCommunity = new int[graph.getNodes().size()];
        for(Node n: graph.getNodes()){// each node starts in its own community
            nodeToCommunity[n.getNodeId()] = n.getNodeId();
        }
        return nodeToCommunity;
    }

    private double[] initDegrees(Graph graph){// initializes the nodes degrees once at creation, degrees of individual nodes never change
        List<Edge> edges = graph.getEdges();
        double[] nodeToDegree = new double[graph.getNodes().size()];
        for(Edge e: edges){
            Node node1 = e.getEndpoints().getNode1();// first endpoint
            Node node2 = e.getEndpoints().getNode2();// second endpoint
            nodeToDegree[node1.getNodeId()] += e.getEdgeWeight();// node degree = the sum of the incident edge weights
            if(!node1.equals(node2))// to avoid adding self-loops twice, self-loops only exist once in the adjacency matrix therefore they're counted once not twice.
                nodeToDegree[node2.getNodeId()] += e.getEdgeWeight();
        }
        return nodeToDegree;
    }

    public int communityOf(Node node){// takes a node and returns the community containing the node
        return this.nodeToCommunity[node.getNodeId()];
    }
    public int communityOf(int nodeId){// takes a node and returns the community containing the node
        return this.nodeToCommunity[nodeId];
    }
    public double degreeOfNode(Node node){// takes a node and returns the degree of the node
        return this.nodeToDegree[node.getNodeId()];
    }
    public double degreeOfNode(int nodeID){// takes a node and returns the degree of the node
        return this.nodeToDegree[nodeID];
    }
    public double degreeOfCommunity(int communityIndex){// takes a community index and returns the degree of the community
        return this.communityDegreeSum[communityIndex];
    }
    public double weightOfCommunity(int communityIndex){// takes a community index and returns the internal weight of the community
        return this.communityWeightSum[communityIndex];
    }
    public int[] getNodeToCommunity() {
        return nodeToCommunity;
    }
    public double[] getNodeToDegree() {
        return nodeToDegree;
    }
    public double[] getCommunityWeightSum() {
        return communityWeightSum;
    }

    public double[] getCommunityDegreeSum() {
        return communityDegreeSum;
    }
}

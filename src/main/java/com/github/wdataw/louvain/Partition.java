package com.github.wdataw.louvain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.github.wdataw.louvain.graph.*;

public class Partition {
    private final int[] nodeToCommunity;// maps each node to its community
    private final double[] nodeToDegree;// maps each node to its degree
    private final double[] communityWeightSum;// Sigma C
    private final double[] communityDegreeSum;// Sigma C hat
    private final Graph graph;

    Partition(Graph graph){
        this.graph = graph;
        this.nodeToCommunity = initCommunities(graph);
        this.nodeToDegree = initDegrees(graph);
        this.communityWeightSum = initCommunityWeights(graph);
        this.communityDegreeSum = initCommunityDegrees(graph);
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

    // At initialization each node is its own community, so NO edge can have
    // both endpoints in the same community (unless it's a self-loop).
    // We still iterate all edges to correctly handle self-loops.
    private double[] initCommunityWeights(Graph graph){ // Angel
        // Array sized by number of nodes (community IDs match node IDs at initialization)
        double[] communityWeightSum = new double[graph.getNodes().size()];
        for(Edge e : graph.getEdges()){
            Node node1 = e.getEndpoints().getNode1();
            Node node2 = e.getEndpoints().getNode2();
            boolean isSelfLoop = node1.equals(node2);
            // An edge counts toward community weight only when both endpoints share the same community
            if(communityOf(node1) == communityOf(node2)){
                int community = communityOf(node1);
                if(isSelfLoop){
                    communityWeightSum[community] += e.getEdgeWeight();// self-loop: count once
                } else {
                    communityWeightSum[community] += 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
                }
            }
        }
        return communityWeightSum;
    }

    // moves a node from its current community to the specified newCommunity
    public void moveNodeToCommunity(Node nodeToMove, int newCommunity){
        if(communityOf(nodeToMove) == newCommunity) return;// no move happens if source and destination are the same

        int nodeId = nodeToMove.getNodeId();
        updateCommunityDegree(nodeId, newCommunity);// update degrees of involved communities
        updateCommunityWeight(nodeId, newCommunity);// update weights of involved communities
        nodeToCommunity[nodeId] = newCommunity;// update node community mapping
    }
    
    
    
    // NOTE: only invoke before actually moving the node to a new community
    // used to update the weights of communities that are involved in a node move (when the node moves from one community to another).
    private void updateCommunityWeight(int nodeId, int newCommunity){
        // For every neighbor of `node`, check if the edge weight needs to be
        // moved from originalCommunity's sum to newCommunity's sum (or removed/added).
        Node nodeToMove = graph.getNodeByID(nodeId);
        int originalCommunity = communityOf(nodeToMove);
        for(Edge e : graph.getAdjList().get(nodeToMove.getNodeId())){
            Node node1 = e.getEndpoints().getNode1();
            Node node2 = e.getEndpoints().getNode2();

            Node neighbor = node1.equals(nodeToMove) ? node2 : node1;// each edge has two endpoints, one of them must be the node itself and the other is its neighbor

            boolean isSelfLoop = neighbor.equals(nodeToMove);
            int neighborCommunity = communityOf(neighbor);

            // neighbor is the nodeToMove itself (self-loop)
            // → directly add the weight to the new community and subtract it from the previous one
            if(isSelfLoop){
                communityWeightSum[originalCommunity] -= e.getEdgeWeight();// self-loop: count once
                communityWeightSum[newCommunity] += e.getEdgeWeight();// self-loop: count once
            }
            // The neighbor was previously with `nodeToMove` in originalCommunity
            // → that edge no longer counts toward originalCommunity's weight
            else if(neighborCommunity == originalCommunity){
                communityWeightSum[originalCommunity] -= 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
            }
            // The neighbor is in newCommunity (or this is a self-loop moving to newCommunity)
            // → that edge now counts toward newCommunity's weight
            else if(neighborCommunity == newCommunity){
                communityWeightSum[newCommunity] += 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
            }
        }
    }

    // NOTE: only invoke before actually moving the node to a new community
    // used to always ensure the total degree is reserved, e.g. +1 degree in one community means -1 in another.
    private void updateCommunityDegree(int nodeId, int newCommunity){
        int originalCommunity = communityOf(nodeId);
        double nodeDegree = degreeOfNode(nodeId);
        communityDegreeSum[newCommunity] += nodeDegree;
        communityDegreeSum[originalCommunity] -= nodeDegree;
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


    //TESTING ONLY
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Partition partition = (Partition) o;
        return Objects.deepEquals(nodeToCommunity, partition.nodeToCommunity);
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

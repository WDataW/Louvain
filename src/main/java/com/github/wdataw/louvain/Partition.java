package com.github.wdataw.louvain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.github.wdataw.louvain.graph.*;

public class Partition {
    private final int[] nodeToCommunity;// maps each node to its community
    private final double[] nodeToDegree;// maps each node to its degree
    private final double[] communityWeightSum;// maps each community to its internal weight
    private final double[] communityDegreeSum;// maps each community to its degree
    private final int[] communitySizes;// maps each community to its size, size as in the number of nodes
    private final Graph graph;

    public Partition(Graph graph){
        this.graph = graph;
        this.nodeToCommunity = initCommunities(graph);// each node starts in its own community
        this.nodeToDegree = initDegrees(graph);// each element i is initialized with node i degree (node degree: sum of all incident edges' weights)
        this.communityWeightSum = initCommunityWeights(graph);// each element i is initialized with community i internal weight (community internal weight: sum of community edges weights)
        this.communityDegreeSum = initCommunityDegrees(graph);// each element i is initialized with community i degree (community degree: sum of community nodes' degrees)
        this.communitySizes = initCommunitySizes();// // each element i is initialized with 1, corresponding to each community starting with 1 node (from initialization)
    }

    // computes the possible modularity gain, for when a node enters a community.
    // used to determine the best community for a node to enter by comparing the gain of the candidate communities
    public double computeModularityGain(Node node, int destinationCommunity, double connectionWeight ){
        return connectionWeight
                - (degreeOfNode(node) * degreeOfCommunity(destinationCommunity))
                / (2.0 * graph.getWeight());
    }

    // removes a node from its current communtiy and places it in an isolated community
    /* purpose: to compute modularity gain, a node must first be removed from its current community for it not to corrupt the real modularity gain
    after that the node is inserted in the community with the highest gain */
    public void removeNodeFromCommunity(Node node){
        moveNodeToCommunity(node, graph.getOrder());// last index is reserved for temporary community swappings
    }

    // initializes community sizes
    private int[] initCommunitySizes(){
        int[] sizes = new int[graph.getOrder() + 1];
        Arrays.fill(sizes, 1);
        return sizes;
    }

    // initializes the degree of each community (community degree: sum of community nodes' degrees)
    private double[] initCommunityDegrees(Graph graph){

        double[] communityDegreeSum = new double[graph.getOrder() + 1];// one extra slot is used as an isolated community, to isolate a node before computing modularity gains
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

        return communityDegreeSum;
    }

    // initializes internal community weights
    private double[] initCommunityWeights(Graph graph){ // Angel
        // Array sized by number of nodes (community IDs match node IDs at initialization)
        double[] communityWeightSum = new double[graph.getOrder() + 1];
        /* At initialization each node is its own community, so NO edge can have
        both endpoints in the same community (unless it's a self-loop). */
        // We still iterate all edges to correctly handle self-loops.
        for(Edge e : graph.getEdges()){
            Node node1 = e.getEndpoints().getNode1();
            Node node2 = e.getEndpoints().getNode2();
            boolean isSelfLoop = node1.equals(node2);
            // at initialization an edge counts toward community weight only if it's a self-loop (since each node starts in its own community)
            if(isSelfLoop) communityWeightSum[communityOf(node1)] += e.getEdgeWeight();
        }
        return communityWeightSum;
    }

    // moves a node from its current community to the specified newCommunity
    public void moveNodeToCommunity(Node nodeToMove, int newCommunity){
        if(communityOf(nodeToMove) == newCommunity) return;// no move happens if source and destination are the same

        int nodeId = nodeToMove.getNodeId();
        updateCommunityDegree(nodeId, newCommunity);// update degrees of involved communities
        updateCommunityWeight(nodeId, newCommunity);// update internal weights of involved communities
        updateCommunitySize(nodeId, newCommunity);// updates the sizes of involved communities
        nodeToCommunity[nodeId] = newCommunity;// update node community mapping
    }
    
    // NOTE: only invoke before actually moving the node to a new community
    // ensures total size is reserved, -1 in size for a community means +1 for another
    private void updateCommunitySize(int nodeId, int newCommunity){
        int originalCommunity = communityOf(nodeId);
        communitySizes[originalCommunity]--;
        communitySizes[newCommunity]++;
    }

    // NOTE: only invoke before actually moving the node to a new community
    // used to update the weights of communities that are involved in a node move (when the node moves from one community to another).
    private void updateCommunityWeight(int nodeId, int newCommunity){
        /* For every neighbor of 'nodeToMove', check if the edge weight needs to be
         subtracted from originalCommunity's sum and added to newCommunity's sum.*/
        Node nodeToMove = graph.getNodeByID(nodeId);
        int originalCommunity = communityOf(nodeToMove);
        for(Edge e : graph.getAdjList().get(nodeToMove.getNodeId())){// for all incident edges
            Node node1 = e.getEndpoints().getNode1();
            Node node2 = e.getEndpoints().getNode2();

            Node neighbor = node1.equals(nodeToMove) ? node2 : node1;// each edge has two endpoints, one of them must be the node itself and the other is its neighbor

            boolean isSelfLoop = neighbor.equals(nodeToMove);// if the neighbor is the node itself (a loop)
            int neighborCommunity = communityOf(neighbor);

            // if it's a self-loop directly add the weight to the newCommunity and subtract it from the previous one (self loops weights are counted once only)
            if(isSelfLoop){
                communityWeightSum[originalCommunity] -= e.getEdgeWeight();// subtract from originalCommunity
                communityWeightSum[newCommunity] += e.getEdgeWeight();// add to newCommunity
            }
            // The neighbor was previously with `nodeToMove` in originalCommunity
            // then that edge no longer counts toward originalCommunity's weight
            else if(neighborCommunity == originalCommunity){
                communityWeightSum[originalCommunity] -= 2 * e.getEdgeWeight();// regular edge: count twice (u→v and v→u)
            }
            // The neighbor is in newCommunity
            // then that edge now counts toward newCommunity's weight
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

    // initializes community mapping, where each node starts in a new community
    private int[] initCommunities(Graph graph){// initializes the communities, each node = a community
        int[] nodeToCommunity = new int[graph.getOrder()];
        for(Node n: graph.getNodes()){// each node starts in its own community
            nodeToCommunity[n.getNodeId()] = n.getNodeId();
        }
        return nodeToCommunity;
    }

    // initializes the nodes degrees once at creation, degrees of individual nodes never change
    private double[] initDegrees(Graph graph){
        List<Edge> edges = graph.getEdges();
        double[] nodeToDegree = new double[graph.getOrder()];
        for(Edge e: edges){
            Node node1 = e.getEndpoints().getNode1();// first endpoint
            Node node2 = e.getEndpoints().getNode2();// second endpoint
            nodeToDegree[node1.getNodeId()] += e.getEdgeWeight();// node degree = the sum of the incident edge weights
            if(!node1.equals(node2))// to avoid adding self-loops twice, self-loops only exist once in the adjacency matrix therefore they're counted only once.
                nodeToDegree[node2.getNodeId()] += e.getEdgeWeight();
        }
        return nodeToDegree;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Partition partition = (Partition) o;
        return Objects.deepEquals(nodeToCommunity, partition.nodeToCommunity);
    }

    // returns the size of a community by communityIndex
    public int sizeOf(int communityIndex){
        return communitySizes[communityIndex];
    }
    // returns the community of a node by Node object
    public int communityOf(Node node){// takes a node and returns the community containing the node
        return this.nodeToCommunity[node.getNodeId()];
    }
    // returns the community of a node by nodeId
    public int communityOf(int nodeId){// takes a node and returns the community containing the node
        return this.nodeToCommunity[nodeId];
    }
    // returns the degree of a node by Node object
    public double degreeOfNode(Node node){// takes a node and returns the degree of the node
        return this.nodeToDegree[node.getNodeId()];
    }
    // returns the degree of a node by nodeId
    public double degreeOfNode(int nodeID){// takes a node and returns the degree of the node
        return this.nodeToDegree[nodeID];
    }
    // returns the degree of a community by communityIndex
    public double degreeOfCommunity(int communityIndex){// takes a community index and returns the degree of the community
        return this.communityDegreeSum[communityIndex];
    }
    // returns the internal weight of a community by communityIndex
    public double weightOfCommunity(int communityIndex){// takes a community index and returns the internal weight of the community
        return this.communityWeightSum[communityIndex];
    }

    // getters
    public int[] getNodeToCommunity() {
        return nodeToCommunity;
    }
    public double[] getCommunityWeightSum() {
        return communityWeightSum;
    }
    public double[] getCommunityDegreeSum() {
        return communityDegreeSum;
    }
    public int[] getCommunitySizes(){
        return communitySizes;
    }
}

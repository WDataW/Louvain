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
        this.adjList = graph.getAdjList();
        this.nodeToCommunity = initCommunities(graph);
        this.nodeToDegree = initDegrees(graph);
        this.communityWeightSum = initCommunityWeights(graph);
//      this.communityDegreeSum = initCommunityDegrees(graph);
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
                communityWeightSum[community] += e.getEdgeWeight();
            }
        }
        return communityWeightSum;
    }

//    private double[] initCommunityDegrees(Graph graph){ Tamim
//
//    }

   public void moveNodeToCommunity(Node node, int newCommunity){// moves a node from its current community to the specified communityIndex
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
             neighborCommunity = oldCommunity;     // self-loop → use saved old community
                } else {
                     neighborCommunity = nodeToCommunity[neighbor.getNodeId()]; // normal edge → look up neighbor's community
                       }

            // The neighbor was previously with `node` in oldCommunity
            // → that edge no longer counts toward oldCommunity's weight
            if(neighborCommunity == oldCommunity){
                communityWeightSum[oldCommunity] -= e.getEdgeWeight();
            }
            // The neighbor is in newCommunity (or this is a self-loop moving to newCommunity)
            // → that edge now counts toward newCommunity's weight
            if(neighborCommunity == newCommunity || isSelfLoop){
                communityWeightSum[newCommunity] += e.getEdgeWeight();
            }
        }
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
    public double degreeOfNode(Node node){// takes a node and returns the degree of the node
        return this.nodeToDegree[node.getNodeId()];
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

}
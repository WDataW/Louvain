package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Edge;
import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.Node;

import java.util.List;

public class Partition {
    private int[] nodeToCommunity;
    private double[] nodeToDegree;
    private double[] communityWeightSum;// Sigma C
    private double[] communityDegreeSum;// Sigma C hat


    Partition(Graph graph){
        this.nodeToCommunity = initCommunities(graph);
        this.nodeToDegree = initDegrees(graph);
//      this.communityWeightSum = initCommunityWeights(graph);
        this.communityDegreeSum = initCommunityDegrees(graph);
    }


//    private double[] initCommunityWeights(Graph graph){ Angel
//
//    }

    
    /*this method is for calculating the sigma C hat 
    when every node in its community*/
    private double[] initCommunityDegrees(Graph graph){ 
        
        /*initilize the communityDegreeSum array 
        with the size of the number of nodes in the community*/
        double[] communityDegreeSum = new double[graph.getNodes().size()]; 
        
        // for each node in the graph
        for(Node node: graph.getNodes()){
            //store the node id
            int nodeId = node.getNodeId();
            //store the degree of that node from the nodeToDegree array
            double degree = this.nodeToDegree[nodeId];
            //store the degree of the node in the communityDegreeSum array
            communityDegreeSum[nodeId] = degree;
        }
        
        //return the array
        return communityDegreeSum;
    }

    public void moveNodeToCommunity(Node node, int communityIndex){// moves a node from its current community to the specified communityIndex
        nodeToCommunity[node.getNodeId()] = communityIndex;

        // update communityDegreeSum for both the involved communities - Tamim

        // update communityWeightSum for both the involved communities - Angel
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

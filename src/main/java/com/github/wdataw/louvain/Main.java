package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.Node;


public class Main {
    public static void main(String[] args) {
        Graph exampleGraph = Graph.getExample();
        Partition communities = new Partition(exampleGraph);

        for(double degree: communities.getCommunityDegreeSum()){
            System.out.println(degree);
        }
        System.out.println();
        Node node = exampleGraph.getNodeByID(0);
        communities.moveNodeToCommunity(node,3);
        Node node2 = exampleGraph.getNodeByID(2);
        communities.moveNodeToCommunity(node2,1);
        for(double degree: communities.getCommunityDegreeSum()){
            System.out.println(degree);
        }
    }
}
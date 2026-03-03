package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.Node;


public class Main {
    public static void main(String[] args) {
        Graph exampleGraph = Graph.getExample();
        Partition communities = new Partition(exampleGraph);

        Node node = exampleGraph.getNodeByID(1);// node number 1
        System.out.println(communities.degreeOfNode(node));
        System.out.println(communities.communityOf(node));

        communities.moveNodeToCommunity(node,2);
        System.out.println(communities.communityOf(node));
    }
}
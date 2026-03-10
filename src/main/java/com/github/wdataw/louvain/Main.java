package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.Node;
import org.w3c.dom.ls.LSOutput;


public class Main {
    public static void main(String[] args) {
        Graph exampleGraph = Graph.getExample();
        Partition communities = new Partition(exampleGraph);

        System.out.println("initial community weights:");
        for (double weight : communities.getCommunityWeightSum()) {
            System.out.println(weight);
        }
        System.out.println("\ninitial community degrees:");
        for (double degree : communities.getCommunityDegreeSum()) {
            System.out.println(degree);
        }
        System.out.println();
        Node node = exampleGraph.getNodeByID(0);
        communities.moveNodeToCommunity(node, 3);
        Node node2 = exampleGraph.getNodeByID(2);
        communities.moveNodeToCommunity(node2, 1);

        System.out.println("final community weights:");
        for (double weight : communities.getCommunityWeightSum()) {
            System.out.println(weight);
        }
        System.out.println("\nfinal community degrees:");
        for (double degree : communities.getCommunityDegreeSum()) {
            System.out.println(degree);
        }

        System.out.println("\ngraph weight = " + exampleGraph.getGraphWeight());

        double modularity = Louvain.modularityOf(exampleGraph, communities);

        System.out.println("\n" + "Modularity is: " + modularity);



    }


}
package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.JSONExporter;
import com.github.wdataw.louvain.graph.Node;


public class Main {
    public static void main(String[] args) {
//        Graph exampleGraph = Graph.readGraph("/p2p-Gnutella05.txt","\t");// 8846 nodes, 31839 edges
//        Graph exampleGraph = Graph.readGraph("/p2p-Gnutella08.txt","\t");// 6301 nodes, 20777 edges
//        Graph exampleGraph = Graph.readGraph("/email-Eu-core.txt"," ");// 1005 nodes, 25571 edges
        Graph exampleGraph = Graph.getExample();// 4 nodes, 5 edges
        Partition communities = new Partition(exampleGraph);
        JSONExporter.toJSON(exampleGraph,communities,"visualization/public/initialGraph.json");
    }
}
package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.Node;
import com.github.wdataw.louvain.graph.visualization.VisualGraph;


public class Main {
    public static void main(String[] args) {
//        Graph emailEUcore = Graph.readGraph("/email-Eu-core.txt");
        Graph emailEUcore = Graph.getExample();
        Partition communities = new Partition(emailEUcore);
        VisualGraph visualGraph = new VisualGraph(emailEUcore, communities);
        visualGraph.display();
    }
}

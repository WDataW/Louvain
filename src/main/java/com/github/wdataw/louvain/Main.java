package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Edge;
import com.github.wdataw.louvain.graph.Endpoints;
import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.Node;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph exampleGraph = Graph.getExample();
        for(Edge e: exampleGraph.getEdges()){
            System.out.println(e.getEndpoints().getNode1().getNodeId()
                              +" <-> "
                              +e.getEndpoints().getNode2().getNodeId()
                              +" W "
                              +e.getEdgeWeight()
            );
        }

        System.out.println(" ");

        Node node = exampleGraph.getNodeByID(3);
        List<Edge> incidentOnNode = exampleGraph.getAdjList().get(node.getNodeId());
        for (Edge e: incidentOnNode){
            Endpoints endpoints = e.getEndpoints();
             System.out.println(endpoints.getNode1().getNodeId()+" <-> "+endpoints.getNode2().getNodeId());
        }

    }
}
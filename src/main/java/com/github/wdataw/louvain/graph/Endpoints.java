package com.github.wdataw.louvain.graph;

public class Endpoints {
    // attributes
    private final Node node1;
    private final Node node2;

    // constructor
    public Endpoints(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    // getters
    public Node getNode1() {
        return node1;
    }
    public Node getNode2() {
        return node2;
    }
}

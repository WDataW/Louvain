package com.github.wdataw.louvain.graph;

import java.util.Objects;

public class Node {
    //attributes
    private final int nodeID;

    //constructors
    public Node(int nodeID) {
        this.nodeID = nodeID;
    }
    
    //getters and setters
    public int getNodeId() {
        return nodeID;
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return nodeID == node.nodeID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nodeID);
    }
}

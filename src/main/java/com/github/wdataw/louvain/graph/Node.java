package com.github.wdataw.louvain.graph;

public class Node {
    //attributes
    private String nodeName;
    private final int nodeID;
    private static int idCounter = 0;
    
    //constructors
    public Node(String nodeName) {
        this.nodeID = ++idCounter;
        this.nodeName = nodeName;
    }
    
    //getters and setters
    public int getNodeId() {
        return nodeID;
    }
    public String getNodeName() {
        return nodeName;
    }
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}

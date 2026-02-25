/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.wdataw.louvain.graph;

/**
 *
 * @author Tamim Masoud Alqurashi
 */
public class Node {
    //attributes
    private String nodeName;
    private final int nodeID;
    private static int nextID = 0;
    
    //constructors
    public Node(String nodeName) {
        this.nodeID = ++nextID;
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

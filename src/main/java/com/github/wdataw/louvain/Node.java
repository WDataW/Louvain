/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.wdataw.louvain;

/**
 *
 * @author Tamim Masoud Alqurashi
 */
public class Node {
    //variables
    private int nodeId;
    private String nodeName;

    
    //constructors

    //id and name
    public Node(int nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }
    
    
    
    
    //getters and setters
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    
    
    
    
}

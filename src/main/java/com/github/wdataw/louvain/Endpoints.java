/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.wdataw.louvain;

/**
 *
 * @author abdulwhid Ghalib
 */
public class Endpoints {
     private Node node1;
    private Node node2;

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

    // setters
    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
    }
}

package com.github.wdataw.louvain;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
       
        Node node1 = new Node("wow");
        Node node2 = new Node("hi");
        
        System.out.println(node1.getNodeId());
        System.out.println(node2.getNodeId());
    }
}
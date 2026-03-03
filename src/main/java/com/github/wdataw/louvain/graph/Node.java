package com.github.wdataw.louvain.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    //attributes
    private String nodeName;
    private final int nodeID;

    //constructors
    public Node(int nodeID) {
        this.nodeID = nodeID;
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

    // extracts nodes from a set of edges
    public static List<Node> extractNodes(List<Edge> edges){// KEY REMOVE
        List<Node> nodes = new ArrayList<Node>();
        for(Edge e:edges){
            // add endpoints to the list of nodes only if they don't already exist in it
            Node n1 = e.getEndpoints().getNode1();
            if(!nodes.contains(n1))nodes.add(n1);
            Node n2 = e.getEndpoints().getNode2();
            if(!nodes.contains(n2))nodes.add(n2);
        }
        return nodes;
    }

}

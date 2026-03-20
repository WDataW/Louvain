package com.github.wdataw.louvain.graph;

import java.util.Objects;

public class Edge {
    // attributes
    private final String edgeID;
    private double edgeWeight;
    private final Endpoints endpoints;

    // constructors
    public Edge(Node node1, Node node2 ,double EdgeWeight) {
        this.edgeWeight = EdgeWeight;
        this.endpoints = new Endpoints(node1,node2);
        this.edgeID = constructId(node1,node2);
    }

    private String constructId(Node node1, Node node2){
        int node1Id = node1.getNodeId();
        int node2Id = node2.getNodeId();
        int a = Math.min(node1Id,node2Id);
        int b = Math.max(node1Id,node2Id);
        return a+"e"+b;
    }

    // setters
    public void setEdgeWeight(double edgeWeight) {
        this.edgeWeight = edgeWeight;
    }

    // getters
    public String getEdgeID() {
        return edgeID;
    }
    public double getEdgeWeight() {
        return edgeWeight;
    }
    public Endpoints getEndpoints(){ return endpoints; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return edgeID == edge.edgeID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(edgeID);
    }
}


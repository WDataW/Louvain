package com.github.wdataw.louvain.graph;

import java.util.Objects;

public class Edge {
    // attributes
    private static int idCounter = 0;
    private final int edgeID;
    private double edgeWeight;
    private final Endpoints endpoints;

    // constructors
    public Edge(Node node1, Node node2 ,double EdgeWeight) {
        this.edgeWeight = EdgeWeight;
        this.endpoints = new Endpoints(node1,node2);
        this.edgeID = idCounter++;
    }

    // setters
    public void setEdgeWeight(double edgeWeight) {
        this.edgeWeight = edgeWeight;
    }

    // getters
    public int getEdgeID() {
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


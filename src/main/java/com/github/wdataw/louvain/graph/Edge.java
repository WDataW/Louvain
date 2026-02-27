package com.github.wdataw.louvain.graph;

public class Edge {
    // attributes
    private static int idCounter = 0;
    private final int edgeID;
    private float edgeWeight;
    private Endpoints endpoints;

    // constructors
    public Edge(float edgeWeight, Endpoints endpoints) {
        this.edgeWeight = edgeWeight;
        this.endpoints = new Endpoints(endpoints.getNode1(), endpoints.getNode2());
        this.edgeID = ++idCounter;
    }
    public Edge(float EdgeWeight, Node node1, Node node2) {
        this.edgeWeight = EdgeWeight;
        this.endpoints = new Endpoints(node1,node2);
        this.edgeID = ++idCounter;
    }

    // setters
    public void setEdgeWeight(float EdgeWeight) {
        this.edgeWeight = EdgeWeight;
    }
    public void setEndpoints(Endpoints endpoints){
        this.endpoints.setNode1(endpoints.getNode1());
        this.endpoints.setNode2(endpoints.getNode2());
    }
    public void setEndpoints(Node node1, Node node2){
        this.endpoints.setNode1(node1);
        this.endpoints.setNode2(node2);
    }

    // getters
    public int getEdgeID() {
        return edgeID;
    }
    public float getEdgeWeight() {
        return edgeWeight;
    }
    public Endpoints getEndpoints(){ return endpoints; }
}


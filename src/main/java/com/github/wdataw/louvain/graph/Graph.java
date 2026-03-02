package com.github.wdataw.louvain.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    // attributes
    // used to initialize graphID
    private static int idCounter = 0;
    // unique identifier of a graph
    private final int graphID;
    // a list of all graph edges / a list of all graph nodes
    private List<Edge> edges = new ArrayList<Edge>();
    private List<Node> nodes = new ArrayList<Node>();
    // an adjacency list structure to represent the graph
    private Map<Node, List<Edge>> adjList = new HashMap<>();

    // constructor
    Graph(List<Edge> edges,List<Node> nodes){
        graphID = ++idCounter;
        this.edges = new ArrayList<Edge>(edges);
        this.nodes = new ArrayList<Node>(nodes);
        this.adjList= toAdjList(this.nodes,this.edges);
    }

    // getters
    public int getGraphID() {
        return graphID;
    }
    public List<Edge> getEdges() {
        return edges;
    }
    public List<Node> getNodes() {
        return nodes;
    }
    public Map<Node, List<Edge>> getAdjList() {
        return adjList;
    }

    // setters
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    // expects a list on nodes and a list of edges to construct and return an adjacency list
    private Map<Node, List<Edge>> toAdjList(List<Node> nodes, List<Edge> edges){
        Map<Node, List<Edge>> adjList = new HashMap<>();
        // create the keys (each node points to an empty list)
        for(Node n:nodes){
        // node -> {}
            adjList.put(n, new ArrayList<>());
        }
        // add each edge to the lists of both its endpoints
        for(Edge e:edges){
        // node -> {edge1,edge2, ...}
          adjList.get(e.getEndpoints().getNode1()).add(e);
          adjList.get(e.getEndpoints().getNode2()).add(e);
        }
        return adjList;
    }
}

package com.github.wdataw.louvain.graph;

import java.io.InputStream;
import java.util.*;

public class Graph {
    // attributes
    private double weight;
    private int size;
    // used to initialize graphID
    private static int idCounter = 0;
    // unique identifier of a graph
    private final int graphID;
    // a list of all graph edges / a list of all graph nodes
    private List<Edge> edges = new ArrayList<Edge>();
    private HashSet<Integer> edgeIds = new HashSet<Integer>();
    private List<Node> nodes = new ArrayList<Node>();
    private HashSet<Integer> nodeIds = new HashSet<Integer>();

    // an adjacency list structure to represent the graph
    private Map<Integer, List<Edge>> adjList = new HashMap<>();

    // constructors
    Graph(List<Edge> edges, List<Node> nodes){
        graphID = ++idCounter;
        this.edges = new ArrayList<Edge>(edges);
        this.nodes = new ArrayList<Node>(nodes);
        this.adjList= toAdjList(this.edges,this.nodes);
        this.weight = getGraphWeight();
        this.size = this.nodes.size();

    }
    Graph(){
        graphID = ++idCounter;
        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
        this.adjList= toAdjList(this.edges,this.nodes);
        this.weight = 0;
        this.size = 0;
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
    public Map<Integer, List<Edge>> getAdjList() {
        return adjList;
    }
    public static Graph getExample(){
        return Graph.readGraph("/example-graph-small.txt"," ");
    }

    public double getWeight(){
        return this.weight;
    }
    public int getSize(){
        return this.size;
    }
    // testing only - KEY REMOVE
    public Node getNodeByID(int nodeID){
        for(Node n:this.nodes){
            if(n.getNodeId() == nodeID)return n;
        }
        return null;
    }

    // setters
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
    public void addNode(Node newNode){
        if(this.nodeIds.contains(newNode.getNodeId()))return;// if node already exists, don't add it
        this.nodeIds.add(newNode.getNodeId());
        this.nodes.add(newNode);
        this.size++;
    }
    public void addEdge(Edge newEdge){
        if(this.edgeIds.contains(newEdge.getEdgeID()))return;// if edge already exists, don't add it
        this.edgeIds.add(newEdge.getEdgeID());
        this.edges.add(newEdge);
    }
    public void updateAdjList(){
        this.adjList = toAdjList(this.edges,this.nodes);
    }
    private void updateGraphWeight(){
        this.weight = getGraphWeight();
    }
    // methods


public double getGraphWeight() { // m
    double totalWeight = 0.0;

    // Iterate through all edges directly
    for (Edge e : edges) {
        Node n1 = e.getEndpoints().getNode1();
        Node n2 = e.getEndpoints().getNode2();

        if (n1.equals(n2)) {
            // Count self-loops once
            totalWeight += e.getEdgeWeight();
        } else {
            // Count regular edges twice
            totalWeight += (e.getEdgeWeight() * 2.0);
        }
    }

    // Normalize by dividing by 2
    return totalWeight / 2.0;
}

// expects a list on nodes and a list of edges to construct and return an adjacency list
    private Map<Integer, List<Edge>> toAdjList(List<Edge> edges,List<Node> nodes){
        Map<Integer, List<Edge>> adjList = new HashMap<>();
        // create the keys (each nodeID points to an empty list)
        for(Node n:nodes){
        // nodeID -> {}
            adjList.put(n.getNodeId(), new ArrayList<>());
        }
        // add each edge to the lists of both its endpoints
        for(Edge e:edges){
        // nodeID -> {edge1,edge2, ...}
            Node node1 =  e.getEndpoints().getNode1();
            List<Edge> incidentOnNode1 = adjList.get(node1.getNodeId());// edges incident on node1
            if(!incidentOnNode1.contains(e))incidentOnNode1.add(e);// if the edge doesn't already exist in incidentOnNode1 then add it there.

            Node node2 =  e.getEndpoints().getNode2();
            List<Edge> incidentOnNode2 = adjList.get(node2.getNodeId());// edges incident on node2
            if(!incidentOnNode2.contains(e))incidentOnNode2.add(e);// if the edge doesn't already exist in incidentOnNode2 then add it there.

        }
        return adjList;
    }

    // expects a file name and converts the file contents into a Graph object with edges and nodes
   public static Graph readGraph(String filename, String delimiter){
        Scanner input = null;

        // loading the graph file from the resources directory
        InputStream inputStream = Graph.class.getResourceAsStream(filename);
        if(inputStream == null){
            throw new IllegalArgumentException("Resource not found: "+filename);
        }
        input = new Scanner(inputStream);

        Graph newGraph = new Graph();// start with an empty graph, then add nodes and edges one by one
        while(input.hasNextLine()){// read the file line by line
            String line = input.nextLine();// each line represents an edge
            String[] edgeComponents = line.split(delimiter);// each line must follow the following structure: "node1ID node2ID weight"

            // construct edge object
            Node node1 = new Node(Integer.parseInt(edgeComponents[0]));
            Node node2 = new Node(Integer.parseInt(edgeComponents[1]));
            float weight = 1f; // if the dataset doesn't provide edge weights, then the weight is 1 by default
            if(edgeComponents.length==3) weight = Float.parseFloat(edgeComponents[2]);

            Edge edge = new Edge(node1,node2,weight);

            // add nodes and edge to their lists
            newGraph.addNode(node1);
            newGraph.addNode(node2);
            newGraph.addEdge(edge);
        }
        newGraph.updateAdjList();// adjList is created when the object is created, then updated when we're done inserting nodes and edges
       newGraph.updateGraphWeight();
       return newGraph;
    }
}

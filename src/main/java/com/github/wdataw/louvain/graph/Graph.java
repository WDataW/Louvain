package com.github.wdataw.louvain.graph;

import java.io.InputStream;
import java.util.*;

public class Graph {
    private double weight;
    private int order;

    private static int idCounter = 0;// used to initialize graphID
    private final int graphID;

    private List<Edge> edges;
    private Map<Integer, Edge> idToEdge = new HashMap<>();// provide O(1) edge lookups

    private List<Node> nodes;
    private Map<Integer, Node> idToNode = new HashMap<>();// provide O(1) node lookups

    // an adjacency list structure to represent the graph
    private Map<Integer, List<Edge>> adjList = new HashMap<>();

    // constructor
    public Graph(){// starts empty, nodes and edges are added later one by one
        graphID = idCounter++;
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.adjList= toAdjList(this.edges,this.nodes);
        this.weight = 0;
        this.order = 0;
    }

    // getters
    public int getGraphID(){
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
    public double getWeight(){
        return this.weight;
    }
    public int getOrder(){
        return this.order;
    }
    public Node getNodeByID(int nodeID){
        return idToNode.get(nodeID);
    }

    // setters
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    // adds a node to the graph, filters out duplicate nodes (duplicate as in having the same id)
    public void addNode(Node newNode){
        if(idToNode.containsKey(newNode.getNodeId()))return;// if node already exists, don't add it
        idToNode.put(newNode.getNodeId(), newNode);
        nodes.add(newNode);
        order++;
    }

    // adds an edge to the graph, filters out duplicate edges (duplicate as in having the same id)
    public void addEdge(Edge newEdge){
        if(idToEdge.containsKey(newEdge.getEdgeID()))return;// if edge already exists, don't add it
        idToEdge.put(newEdge.getEdgeID(),newEdge);
        edges.add(newEdge);
    }

    // updates the adjacency list after nodes/edges are added to the graph
    // NOTE: must be invoked manually, to avoid updating it EV times when reading a graph from a file
    public void updateAdjList(){
        this.adjList = toAdjList(this.edges,this.nodes);
    }

    // updates graph weight after nodes/edges are added to the graph
    // NOTE: must be invoked manually, to avoid updating it EV times when reading a graph from a file
    public void updateGraphWeight(){
        this.weight = computeGraphWeight();
    }

    // calculates the total graph weight
    public double computeGraphWeight() {
        double totalWeight = 0.0;

        // iterate through all edges directly
        for (Edge e : edges) {
            Node n1 = e.getEndpoints().getNode1();// endpoint1
            Node n2 = e.getEndpoints().getNode2();// endpoint2

            if (n1.equals(n2)) {
                // count self-loops once
                totalWeight += e.getEdgeWeight();
            } else {
                // count regular edges twice
                totalWeight += (e.getEdgeWeight() * 2.0);
            }
        }

        // weight = sum of adj matrix entries / 2
        return totalWeight / 2.0;
    }

    // constructs an adjacency list using a list of edges and list of nodes
    private Map<Integer, List<Edge>> toAdjList(List<Edge> edges,List<Node> nodes){
        Map<Integer, List<Edge>> adjList = new HashMap<>();

        for(Node n:nodes){// for each node
            // create a key in adjList, which will later point to a list of incident edges
            adjList.put(n.getNodeId(), new ArrayList<>());// nodeId -> {}
        }

        for(Edge e:edges){// for each edge
            // add the edge to the lists of both its endpoints
            // endpoint1 -> {{1,2}, ...}
            // endpoint2 -> {{1,2}, ...}

            int node1Id =  e.getEndpoints().getNode1().getNodeId();// first endpoint
            List<Edge> edgesIncidentOnNode1 = adjList.get(node1Id);// edges incident on the first endpoint

            // if the edge doesn't already exist in edgesIncidentOnNode1 then add it there.
            if(!edgesIncidentOnNode1.contains(e))edgesIncidentOnNode1.add(e);

            int node2Id =  e.getEndpoints().getNode2().getNodeId();// second endpoint
            List<Edge> edgesIncidentOnNode2 = adjList.get(node2Id);// edges incident on the second endpoint

            // if the edge doesn't already exist in edgesIncidentOnNode2 then add it there.
            if(!edgesIncidentOnNode2.contains(e))edgesIncidentOnNode2.add(e);
        }
        return adjList;
    }

    // expects a file name, and a delimiter then converts the file contents to a Graph object with edges and nodes
    // NOTE: file must be structured as:
    // endpoint1:integer endpoint2:integer weight(optional):number
    // example: 0 2 => this line represents an edge, where the delimiter is " ", and weight defaults to 1
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
            String[] edgeComponents = line.split(delimiter);// split the line based on the delimiter

            // construct nodes/edge object
            Node node1 = new Node(Integer.parseInt(edgeComponents[0]));
            Node node2 = new Node(Integer.parseInt(edgeComponents[1]));
            float weight = 1f; // if the dataset doesn't provide edge weights, then the weight is 1 by default
            if(edgeComponents.length==3) weight = Float.parseFloat(edgeComponents[2]);// if weight is provided then use it

            Edge edge = new Edge(node1,node2,weight);

            // add nodes and edge to the graph
            newGraph.addNode(node1);
            newGraph.addNode(node2);
            newGraph.addEdge(edge);
        }
        newGraph.updateAdjList();// adjList is created when the Graph object is created, then updated manually when we're done inserting nodes and edges
        newGraph.updateGraphWeight();// graph weight starts as 0, then updated manually when we're done inserting nodes and edges
        System.out.println("Graph read succesfully.");
        return newGraph;
    }
}

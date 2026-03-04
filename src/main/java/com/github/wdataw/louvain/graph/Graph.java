package com.github.wdataw.louvain.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

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
    private Map<Integer, List<Edge>> adjList = new HashMap<>();

    // constructors
    Graph(List<Edge> edges){// KEY REMOVE
        graphID = ++idCounter;
        this.edges = new ArrayList<Edge>(edges);
        this.nodes = Node.extractNodes(this.edges);
        this.adjList= toAdjList(this.edges,this.nodes);
    }
    Graph(List<Edge> edges, List<Node> nodes){
        graphID = ++idCounter;
        this.edges = new ArrayList<Edge>(edges);
        this.nodes = new ArrayList<Node>(nodes);
        this.adjList= toAdjList(this.edges,this.nodes);
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
        return Graph.readGraph("/example-graph-small.txt");
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
        if(this.nodes.contains(newNode))return;// if node already exists, don't add it
        this.nodes.add(newNode);
    }
    public void addEdge(Edge newEdge){
        if(this.edges.contains(newEdge))return;// if edge already exists, don't add it
        this.edges.add(newEdge);
    }
    public void updateAdjList(){
        this.adjList = toAdjList(this.edges,this.nodes);
    }

    // methods

   public double getGraphWeight() {
    double totalWeight = 0.0;
    
    for (List<Edge> edges : adjList.values()) {
        for (Edge e : edges) {
            Node n1 = e.getEndpoints().getNode1();
            Node n2 = e.getEndpoints().getNode2();
            
            if (n1.equals(n2)) {
                // Add self-loops once
                totalWeight += e.getEdgeWeight();
            } else {
                // Add half weight for normal edges (counted twice)
                totalWeight += (e.getEdgeWeight() / 2.0);
            }
        }
    }
    return totalWeight;
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
    public static Graph readGraph(String filename){
        Scanner input = null;

        // loading the graph file from the resources directory
        InputStream inputStream = Graph.class.getResourceAsStream(filename);
        if(inputStream == null){
            throw new IllegalArgumentException("Resource not found: "+filename);
        }
        input = new Scanner(inputStream);

        Graph newGraph = new Graph(new ArrayList<Edge>(),new ArrayList<Node>());// start with an empty graph, then add nodes and edges one by one
        while(input.hasNextLine()){// read the file line by line
            String line = input.nextLine();// each line represents an edge
            String[] edgeComponents = line.split(" ");// each line must follow the following structure: "node1ID node2ID weight"

            // construct edge object
            Node node1 = new Node(Integer.parseInt(edgeComponents[0]));
            Node node2 = new Node(Integer.parseInt(edgeComponents[1]));
            float weight = Float.parseFloat(edgeComponents[2]);
            Edge edge = new Edge(node1,node2,weight);

            // add nodes and edge to their lists
            newGraph.addNode(node1);
            newGraph.addNode(node2);
            newGraph.addEdge(edge);
        }
        newGraph.updateAdjList();// adjList is created when the object is created, then updated when we're done inserting nodes and edges
        return newGraph;
    }
}

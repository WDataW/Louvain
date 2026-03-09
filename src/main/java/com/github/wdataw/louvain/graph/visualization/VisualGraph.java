package com.github.wdataw.louvain.graph.visualization;

import com.github.wdataw.louvain.Partition;
import com.github.wdataw.louvain.graph.*;
import org.graphstream.graph.implementations.MultiGraph;

import java.awt.*;

public class VisualGraph {
    public static int idCounter = 0;
    private final int id;
    private final MultiGraph visualGraph;
    private final int[] nodeToCommunity;

    public VisualGraph(Graph graph, Partition communities){
        this.visualGraph = normalize(graph);
        this.nodeToCommunity = communities.getNodeToCommunity();
        config();
        colorizeCommunities(graph);
        this.id = idCounter++;
    }
    public void display(){
        visualGraph.display();
    }

    private void colorizeCommunities(Graph graph){
        for(Node n:graph.getNodes()){
            int nodeId = n.getNodeId();
            int nodeCommunity = nodeToCommunity[nodeId];
            float hue = (nodeCommunity * 0.6180339887f) % 1;
            Color communityColor = Color.getHSBColor(hue,0.8f,0.9f);

            String style = String.format("fill-color:rgb(%d,%d,%d);", communityColor.getRed(),communityColor.getGreen(),communityColor.getBlue());

            org.graphstream.graph.Node node = visualGraph.getNode(stringify(nodeId));
            node.setAttribute("ui.style",style);
        }
    }

    private void config(){
        System.setProperty("org.graphstream.ui", "swing");
        visualGraph.setAttribute("ui.quality");
        visualGraph.setAttribute("ui.antialias");
        visualGraph.setAttribute("layout.force",2);
        visualGraph.setAttribute("layout.quality",4);
        visualGraph.setAttribute("ui.stylesheet", """
                graph{
                    fill-color:black;
                    padding:100px;
                }
                node{
                    fill-color:white;
                    size:20px;
                    text-size:20;
                    text-alignment:center;
                    text-color:black;
                }
                edge{
                    size:0.5px;
                    fill-color:white;
                }
                
                """);
    }
    private MultiGraph normalize(Graph graph){
        MultiGraph visualGraph  = new MultiGraph(stringify(id));
        for (Node n: graph.getNodes()){
            org.graphstream.graph.Node node = visualGraph.addNode(stringify(n.getNodeId()));
            node.setAttribute("ui.label",stringify(n.getNodeId()));

        }
        for(Edge e: graph.getEdges()){
            String node1Id = stringify(e.getEndpoints().getNode1().getNodeId());
            String node2Id = stringify(e.getEndpoints().getNode2().getNodeId());
            String edgeId = node1Id +"edge"+ node2Id;

            visualGraph.addEdge(edgeId,node1Id,node2Id);
        }
        return visualGraph;
    }
    private String stringify(int id){
        return String.valueOf(id);
    }

    public int getId() {
        return this.id;
    }
}

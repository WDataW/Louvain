package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Edge;
import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.JSONExporter;
import com.github.wdataw.louvain.graph.Node;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);// essential don't remove.
        // Graph graph = Graph.readGraph("/p2p-Gnutella31.txt", "\t");
        // Graph graph = Graph.readGraph("/deezer_europe.csv",",");
        // Graph graph = Graph.readGraph("/facebook_combined.txt"," ");
        // Graph graph = Graph.readGraph("/CA-AstroPh.txt","\t");
        // Graph graph = Graph.readGraph("/musae_PTBR_edges.csv",",");
        // Graph graph = Graph.readGraph("/BioGrid-Arabidopsis-Thaliana-Columbia.txt", " ");
        // Graph graph = Graph.readGraph("/musae_facebook_edges.csv", ",");
        // Graph graph = Graph.readGraph("/petster-friendships-hamster-uniq", " ");
        Graph graph = Graph.readGraph("/video-example.txt", " ");
        List<Map<Integer, Set<Integer>>> dendrogram = Louvain.louvain(graph);
        Louvain.printDendrogram(dendrogram);
    }
}

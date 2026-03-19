package com.github.wdataw.louvain;

import com.github.wdataw.louvain.graph.Edge;
import com.github.wdataw.louvain.graph.Graph;
import com.github.wdataw.louvain.graph.JSONExporter;
import com.github.wdataw.louvain.graph.Node;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);// essential don't remove.
//        Graph graph = Graph.readGraph("/p2p-Gnutella31.txt","\t");// 62586 nodes, 147892 edges
//        Graph graph = Graph.readGraph("/deezer_europe.csv",",");// 28281 nodes, 92752 edges
//        Graph graph = Graph.readGraph("/facebook_combined.txt"," ");// 4039 nodes, 88234 edges
        Graph graph = Graph.readGraph("/video-example.txt", " ");
        List<Map<Integer, Set<Integer>>> dendrogram =  Louvain.louvain(graph,"test");


//      print dendogram
        for(int i=0; i<dendrogram.size();i++){
            Map<Integer, Set<Integer>> currentLevel = dendrogram.get(i);
            System.out.println("Level "+i);
            for(int j:currentLevel.keySet()){
                int superNodeId = j;
                Set<Integer> originalNodeIds = currentLevel.get(superNodeId);
                System.out.print(superNodeId+ " -> ");
                for(int k:originalNodeIds)
                    System.out.print(k+" ");
                System.out.println();
            }
            System.out.println();
        }
    }
}
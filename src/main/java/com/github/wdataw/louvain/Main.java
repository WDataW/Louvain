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
//        Graph graph = Graph.readGraph("/p2p-Gnutella05.txt","\t");// 8846 nodes, 31839 edges
//        Graph graph = Graph.readGraph("/p2p-Gnutella08.txt","\t");// 6301 nodes, 20777 edges
//        Graph graph = Graph.readGraph("/email-Eu-core.txt"," ");// 1005 nodes, 25571 edges
        Graph graph = Graph.readGraph("/video-example.txt", " ");
        List<Map<Integer, Set<Integer>>> dendogram =  Louvain.louvain(graph,"video-example");


//      print dendogram
        for(int i=0; i<dendogram.size();i++){
            Map<Integer, Set<Integer>> currentLevel = dendogram.get(i);
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
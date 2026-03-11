/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.Graph;


/**
 *
 * @author Tkmyg12345 & flowglow
 */
public class Louvain {
    public static double modularityOf(Graph graph, Partition communities) {
        double m = graph.getGraphWeight();
        double modularity = 0.0;

        double[] communityWeights = communities.getCommunityWeightSum();
        double[] communityDegrees = communities.getCommunityDegreeSum();

        for (int c = 0; c < communityWeights.length; c++) {

            double sigmaC = communityWeights[c];
            double sigmaHatC = communityDegrees[c];

            modularity += (sigmaC / (2 * m)) - Math.pow((sigmaHatC / (2 * m)), 2);
        }
        return modularity;
    }
}

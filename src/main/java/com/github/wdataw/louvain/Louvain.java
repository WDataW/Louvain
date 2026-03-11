package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.Graph;


public class Louvain {
    public static double modularityOf(Graph graph, Partition communities) {
        double m = graph.getGraphWeight();
        double modularity = 0.0;

        double[] communityWeights = communities.getCommunityWeightSum();
        double[] communityDegrees = communities.getCommunityDegreeSum();

        for (int c = 0; c < communityWeights.length; c++) {
            double sigmaCHat = communityDegrees[c];
            if(sigmaCHat == 0) continue; // if a community has a degree of 0 then it is either isolated or empty, and therefore won't affect the modularity

            double sigmaC = communityWeights[c];
            double twoM = 2*m;
            modularity += (1/twoM) * (sigmaC - (sigmaCHat * sigmaCHat) / twoM);
        }
        return modularity;
    }
}

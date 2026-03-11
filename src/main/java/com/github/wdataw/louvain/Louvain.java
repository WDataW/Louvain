package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.*;

import java.util.HashSet;
import java.util.Set;


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


    public Partition optimize(Graph graph, Partition communities) {

        boolean improvement = true;

        while (improvement) {

            improvement = false;

            for (Node node : graph.getNodes()) {

                int originalCommunity = communities.communityOf(node);
                double bestModularity = modularityOf(graph, communities);
                int bestCommunity = originalCommunity;

                // collect neighbor communities
                Set<Integer> neighborCommunities = new HashSet<>();

                for (Edge e : graph.getAdjList().get(node.getNodeId())) {

                    Node n1 = e.getEndpoints().getNode1();
                    Node n2 = e.getEndpoints().getNode2();

                    Node neighbor = n1.equals(node) ? n2 : n1;

                    neighborCommunities.add(communities.communityOf(neighbor));
                }

                // try moving node to each neighbor community
                for (int candidateCommunity : neighborCommunities) {

                    if (candidateCommunity == originalCommunity)
                        continue;

                    // move node
                    communities.moveNodeToCommunity(node, candidateCommunity);

                    double newModularity = modularityOf(graph, communities);

                    if (newModularity > bestModularity) {
                        bestModularity = newModularity;
                        bestCommunity = candidateCommunity;
                    }

                    // move back
                    communities.moveNodeToCommunity(node, originalCommunity);
                }

                // apply best move
                if (bestCommunity != originalCommunity) {

                    communities.moveNodeToCommunity(node, bestCommunity);
                    improvement = true;
                }
            }
        }

        return communities;
    }
}

package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.*;

import java.util.HashMap;
import java.util.Map;



public class Louvain {

    //this method is for calculating the modularity Q of a given graph

    public static double modularityOf(Graph graph, Partition communities) {
        double m = graph.getGraphWeight();
        double modularity = 0.0;

        double[] communityWeights = communities.getCommunityWeightSum();
        double[] communityDegrees = communities.getCommunityDegreeSum();

        for (int c = 0; c < communityWeights.length; c++) {
            double sigmaCHat = communityDegrees[c];
            if (sigmaCHat == 0)
                continue; // if a community has a degree of 0 then it is either isolated or empty, and therefore won't affect the modularity

            double sigmaC = communityWeights[c];
            double twoM = 2 * m;
            modularity += (1 / twoM) * (sigmaC - (sigmaCHat * sigmaCHat) / twoM);
        }
        return modularity;
    }

    //this method is for optimizing a given graph and returning a new partition with higher modularity

    public static Partition optimize(Graph graph, Partition communities) {

        boolean improvement = true;
        double m = graph.getGraphWeight();

        while (improvement) {

            improvement = false;

            for (Node node : graph.getNodes()) {

                int nodeId = node.getNodeId();
                int originalCommunity = communities.communityOf(node);
                double nodeDegree = communities.degreeOfNode(node);

                Map<Integer, Double> communityWeights = new HashMap<>();

                for (Edge e : graph.getAdjList().get(nodeId)) {

                    Node n1 = e.getEndpoints().getNode1();
                    Node n2 = e.getEndpoints().getNode2();

                    Node neighbor;

                    if (n1.equals(node)) {
                        neighbor = n2;
                    } else {
                        neighbor = n1;
                    }

                    int neighborCommunity = communities.communityOf(neighbor);

                    communityWeights.put(neighborCommunity, communityWeights.getOrDefault(neighborCommunity, 0.0) + e.getEdgeWeight());
                }

                //weight of edges from node i to its original community
                double k_i_in_old = communityWeights.getOrDefault(originalCommunity, 0.0);

                double oldSigmaCHat = communities.degreeOfCommunity(originalCommunity);

                int bestCommunity = originalCommunity;
                double bestGain = 0;

                for (Map.Entry<Integer, Double> entry : communityWeights.entrySet()) {

                    int candidateCommunity = entry.getKey();
                    //the weight of edges from the node to the new community.
                    double k_i_in = entry.getValue();

                    double newSigmaCHat = communities.degreeOfCommunity(candidateCommunity);

                    double gain = (k_i_in / m) - (nodeDegree * newSigmaCHat) / (2 * m * m) - (k_i_in_old / m) + (nodeDegree * oldSigmaCHat) / (2 * m * m);

                    if (gain > bestGain) {
                        bestGain = gain;
                        bestCommunity = candidateCommunity;
                    }
                }

                if (bestCommunity != originalCommunity) {

                    communities.moveNodeToCommunity(node, bestCommunity);
                    improvement = true;
                }
            }
        }

        return communities;
    }



}

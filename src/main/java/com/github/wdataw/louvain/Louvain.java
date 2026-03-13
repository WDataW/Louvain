package com.github.wdataw.louvain;
import com.github.wdataw.louvain.graph.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Louvain {
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


    public static Partition optimize(Graph graph, Partition communities) {

        boolean canimprove = true;

        while (canimprove) {

            canimprove = false;

            for (Node node : graph.getNodes()) {

                int originalCommunity = communities.communityOf(node);
                double bestModularity = modularityOf(graph, communities);
                int bestCommunity = originalCommunity;

                // collect neighbor communities
                Set<Integer> neighborCommunities = new HashSet<>();

                for (Edge edge : graph.getAdjList().get(node.getNodeId())) {

                    Node node1 = edge.getEndpoints().getNode1();
                    Node node2 = edge.getEndpoints().getNode2();

                    Node neighbor;
                    if (node1.equals(node)) {
                        neighbor = node2;
                    } else {
                        neighbor = node1;
                    }

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
                    canimprove = true;
                }
            }
        }

        return communities;
    }


    /*public static Partition optimize(Graph graph, Partition communities) {

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

                    Node neighbor = n1.equals(node) ? n2 : n1;
                    int neighborCommunity = communities.communityOf(neighbor);

                    communityWeights.put(
                            neighborCommunity,
                            communityWeights.getOrDefault(neighborCommunity, 0.0)
                                    + e.getEdgeWeight()
                    );
                }

                int bestCommunity = originalCommunity;
                double bestGain = 0;

                for (Map.Entry<Integer, Double> entry : communityWeights.entrySet()) {

                    int candidateCommunity = entry.getKey();
                    double k_i_in = entry.getValue();

                    double sigmaTot = communities.degreeOfCommunity(candidateCommunity);

                    double gain =
                            (k_i_in / (2 * m)) -
                                    (nodeDegree * sigmaTot) / ((2 * m) * (2 * m));

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
    }*/
}

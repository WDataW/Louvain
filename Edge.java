
package louvain;

/**
 *
 * @author doom4
 */
public class Edge {

    private static int idCounter = 0;
    private final int EdgeID;
    private float EdgeWeight;
    private Endpoints endpoint1;
    private Endpoints endpoint2;

    public void setEdgeWeight(float EdgeWeight) {
        this.EdgeWeight = EdgeWeight;
    }

    public void setEndpoint1(Endpoints endpoint1) {
        this.endpoint1 = endpoint1;
    }

    public void setEndpoint2(Endpoints endpoint2) {
        this.endpoint2 = endpoint2;
    }

    public Edge(float EdgeWeight, Endpoints endpoint1, Endpoints endpoint2) {
        this.EdgeWeight = EdgeWeight;
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
        this.EdgeID = ++idCounter;
        
    }

    public int getEdgeID() {
        return EdgeID;
    }

    public float getEdgeWeight() {
        return EdgeWeight;
    }

    public Endpoints getVertex1() {
        return endpoint1;
    }

    public Endpoints getVertex2() {
        return endpoint2;
    }
    
}


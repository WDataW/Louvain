
package louvain1;

/**
 *
 * @author doom4
 */
public class Edge {

    private static int idCounter = 0;
    private final int EdgeID;
    private float EdgeWeight;
    private Endpoints endpoint1;
    
    //setters
    public void setEdgeWeight(float EdgeWeight) {
        this.EdgeWeight = EdgeWeight;
    }

     //constructor
    public Edge(float EdgeWeight, Endpoints endpoint1) {
        this.EdgeWeight = EdgeWeight;
        this.endpoint1 = endpoint1;
        this.EdgeID = ++idCounter;
    }
    
     //getters
    public int getEdgeID() {
        return EdgeID;
    }

    public float getEdgeWeight() {
        return EdgeWeight;
    }
}


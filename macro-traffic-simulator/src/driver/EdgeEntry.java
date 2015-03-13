package driver;

import network.Edge;

/**
 * A structure class used by <code>ShortestPath</code> class to calculate the
 * paths/rotues between an origin and destination
 *
 * @author Ricardo Grunitzki
 */
public class EdgeEntry {

    /**
     * Edge object representing a road
     */
    private Edge edge;
    /**
     * float value representing the cost in the road.
     */
    private float cost;

    /**
     * EdgeEntry constructor
     *
     * @param edge object representing a road.
     * @param cost float value representing the cost in the road
     */
    public EdgeEntry(Edge edge, float cost) {
        this.edge = edge;
        this.cost = cost;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}

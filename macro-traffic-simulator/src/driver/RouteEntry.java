package driver;

import network.Edge;

/**
 * Structure to represent an edge in a route/path.
 *
 * @author Ricardo Grunitzki
 */
public class RouteEntry {

    /**
     * Edge object representing the road.
     */
    private Edge edge;
    /**
     * float value representing the cost to travel in the road.
     */
    private float cost;

    /**
     * RouteEntry constructor
     *
     * @param edge object representing the road.
     * @param cost float value representing the cost to travel in the road.
     */
    public RouteEntry(Edge edge, float cost) {
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

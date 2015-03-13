package driver;

import java.util.List;
import network.Edge;

/**
 * A structure to represent a route in a road network
 *
 * @author Ricardo Grunitzki
 */
public class Route {

    /**
     * a List of edges representing the path between the route's origin and
     * destination.
     */
    private List<Edge> path;
    /**
     * float value representing the route's cost.
     */
    private float cost;
    /**
     * String value representing the route's name.
     */
    private String name;

    /**
     * Route constructor.
     * @param path List of edges representing the path between the route's origin and destination
     * @param cost The route's cost
     */
    public Route(List<Edge> path, float cost) {
        this.path = path;
        this.cost = cost;
        this.name = path.get(0).getFrom().toString();
        for (Edge e : path) {
            this.name += e.getTo().toString();
        }
    }

   
    public List<Edge> getPath() {
        return path;
    }

    public void setPath(List<Edge> path) {
        this.path = path;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

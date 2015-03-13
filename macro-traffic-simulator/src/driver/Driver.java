package driver;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import network.Edge;
import network.Graph;
import network.Vertex;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 * @author Gabriel de Oliveira Ramos
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class Driver<T extends Driver> implements Callable<Driver> {

    /**
     * integer identifier.
     */
    protected int id;

    /**
     * vertex OD origin.
     */
    protected Vertex origin;

    /**
     * vertex OD destination.
     */
    protected final Vertex destination;

    /**
     * graph containing the road network topology.
     */
    protected Graph graph;

    /**
     * vertex representing current driver's intersection.
     */
    protected Vertex current_intersection;

    /**
     * edge representing current driver's road.
     */
    protected Edge current_road;

    /**
     * route traveled by driver.
     */
    protected List<RouteEntry> traveledRoute;

    /**
     * random variable which must be removed in the future.
     */
    protected Random random;

    /**
     * boolean step identifier for the call method which must be removed in the
     * feature.
     */
    private boolean is_step_a = true;

    /**
     * float current driver's travel time.
     */
    protected float travelTime; //route choice parameter

    /**
     *
     * @param id String identifier
     * @param origin Vertex origin
     * @param destination Vertex destination
     * @param graph Graph representing the road network
     */
    protected Driver(int id, Vertex origin, Vertex destination, Graph graph) {

        if (origin == destination) {
            System.err.println("Origin and destination cannot be the same!");
        }

        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.graph = graph;
        random = new Random(Params.RANDOM.nextLong());
    }

    /**
     * Actions to be performed by each agent. Note: two steps are available
     * because some operations take into consideration what was made by all
     * agents.
     */
    public abstract void step_A();

    /**
     * Actions that must be performed only after all agents have finished
     * step_A. Note: two steps are available because some operations take into
     * consideration what was made by all agents.
     */
    public abstract void step_B();

    /**
     * Actions to be performed by each agent before the episode starts.
     */
    public abstract void beforeEpisode();

    /**
     * Actions to be performed by each agent after the episode ends.
     */
    public abstract void afterEpisode();

    /**
     * Prints the driver's traveled route.
     */
    public void printRoute() {
        if (traveledRoute.size() < 1) {
            System.out.println(">>>>>>>>>>>>> " + this.toString() + " ---- " + this.getTravelTime());
        }
        String str = this.toString() + ": " + traveledRoute.get(0).getEdge().getFrom();
        float sum = 0f;
        for (RouteEntry e : traveledRoute) {
            str += " > " + e.getEdge().getTo();
            //System.out.println(e.e + " = " + e.v);
            sum += e.getCost();
        }
        str += " (total=" + sum + ")";
        //System.out.println("Total = " + sum);
        System.out.println(str);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean printODpair) {
        return "driver" + id + (printODpair ? " (" + origin + destination + ")" : "");
    }

    /**
     * Returns the current driver's travel time in minutes
     *
     * @return a float value representing the minutes
     */
    public float getTravelTime() {
        float sum = 0;
        for (RouteEntry e : traveledRoute) {
            sum += e.getEdge().getAcumulatedCost();
        }
        return sum;
    }

    /**
     * Verifies if the driver has arrived in its destination
     *
     * @return a boolean representation
     */
    public boolean hasArrived() {
        if (traveledRoute.isEmpty()) {
            return false;
        }
        return traveledRoute.get(traveledRoute.size() - 1).getEdge().getTo() == destination;
    }

    /**
     * Resets the driver's attributes.
     */
    public void reset() {
        this.current_intersection = origin;
        this.current_road = null;
        this.is_step_a = true;
        this.traveledRoute = new LinkedList<>();
        this.travelTime = 0;
    }

    /**
     * Resets the drivers attributes. May be overrided by the extended class.
     */
    public void resetAll() {
        reset();
    }

    @Override
    public Driver call() throws Exception {
        if (this.is_step_a) {
            this.step_A();
            this.is_step_a = false;
        } else {
            this.step_B();
            this.is_step_a = true;
        }
        return this;
    }

    /**
     * Verifies if driver needs be lunched or not. its true by default. Must be
     * override if needed.
     *
     * @return a boolean flag.
     */
    public boolean mustBeProcessed() {
        return true;
    }

    /**
     * Returns a string representing the driver's OD pair.
     *
     * @return a formated String 'O-D'.
     */
    public String getODPair() {
        return this.origin.getId() + "-" + this.destination.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public void setOrigin(Vertex origin) {
        this.origin = origin;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public Vertex getCurrent_intersection() {
        return current_intersection;
    }

    public void setCurrent_intersection(Vertex current_intersection) {
        this.current_intersection = current_intersection;
    }

    public Edge getCurrent_road() {
        return current_road;
    }

    public void setCurrent_road(Edge current_road) {
        this.current_road = current_road;
    }

    public List<RouteEntry> getTraveledRoute() {
        return traveledRoute;
    }

    public void setTraveledRoute(List<RouteEntry> traveledRoute) {
        this.traveledRoute = traveledRoute;
    }

    public void setTravelTime(float travelTime) {
        this.travelTime = travelTime;
    }

}

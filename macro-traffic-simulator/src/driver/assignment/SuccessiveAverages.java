package driver.assignment;

import driver.Driver;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import network.Edge;
import driver.EdgeEntry;
import network.Graph;
import driver.Route;
import driver.RouteEntry;
import network.Vertex;
import simulation.Params;
import util.ShortestPath;

/**
 * @author Ricardo Grunitzki
 */
public class SuccessiveAverages extends Driver {

    public SuccessiveAverages(int id, Vertex origin, Vertex destination, Graph G) {
        super(id, origin, destination, G);
        this.resetAll();
    }

    private Route route;

    @Override
    public void resetAll() {
        super.resetAll();
        ShortestPath shortestPath = new ShortestPath();

        Map<String, EdgeEntry> edgesMap;
        edgesMap = new Hashtable<>();
        for (Edge e : this.graph.getEdges()) {
            edgesMap.put(e.toString(), new EdgeEntry(e, e.msaCost()));
        }

        List<Edge> path = shortestPath.dijkstra(this.graph.getVertex(), edgesMap, this.origin, this.destination);
        route = new Route(path, shortestPath.calcCostPath(edgesMap, path));
    }

    @Override
    public void reset() {
        super.reset(); //To change body of generated methods, choose Tools | Templates.
        this.travelTime = 0;
    }

    @Override
    public void step_A() {
        current_road = route.getPath().get(Params.STEP - 1);
        current_intersection = current_road.getTo();
    }

    @Override
    public void step_B() {
        this.traveledRoute.add(new RouteEntry(current_road, current_road.msaCost()));
        this.travelTime += current_road.msaCost();
//        this.traveledRoute.add(new RouteEntry(current_road, current_road.getCost()));
//        this.travelTime += current_road.getCost();

        if (hasArrived()) {
            this.current_road = null;
        }
    }

    @Override
    public void beforeEpisode() {
        this.resetAll();
    }

    @Override
    public void afterEpisode() {
//        ((RouteEntry)this.traveledRoute.get(this.traveledRoute.size()-1)).setCost(((RouteEntry)this.traveledRoute.get(this.traveledRoute.size()-1)).getEdge().msaCost());
    }
    
    @Override
    public float getTravelTime() {
        float cost = 0f;
        for (Object entry: this.traveledRoute){
            cost+= ((RouteEntry) entry).getEdge().msaCost();
        }
        return cost;
    }

}

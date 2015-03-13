/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.assignment;

import driver.Driver;
import java.util.Hashtable;
import util.ShortestPath;
import java.util.List;
import java.util.Map;
import network.Edge;
import driver.EdgeEntry;
import network.Graph;
import driver.Route;
import driver.RouteEntry;
import network.Vertex;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class AllOrNothing extends Driver {

    private Route route;

    public AllOrNothing(int id, Vertex origin, Vertex destination, Graph G) {
        super(id, origin, destination, G);
        this.resetAll();
    }

    @Override
    public void resetAll() {
        super.resetAll(); //To change body of generated methods, choose Tools | Templates.

        ShortestPath shortestPath = new ShortestPath();
        //Map<String, EdgeEntry> edgesMap = shortestPath.listEdgeToHashtable(this.graph.getEdges(), true);

        Map<String, EdgeEntry> edgesMap = new Hashtable<>();
        for (Edge e : this.graph.getEdges()) {
            edgesMap.put(e.toString(), new EdgeEntry(e, e.getFreeFlowCost()));
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
        this.traveledRoute.add(new RouteEntry(current_road, current_road.getCost()));
        this.travelTime += current_road.getCost();

        if (hasArrived()) {
            this.current_road = null;
        }
    }

    @Override
    public void beforeEpisode() {

    }

    @Override
    public void afterEpisode() {
    }
    
    @Override
    public float getTravelTime() {
        float cost = 0f;
        for (Object entry: this.traveledRoute){
            cost+= ((RouteEntry) entry).getEdge().getAcumulatedCost();
        }
        return cost;
    }
}

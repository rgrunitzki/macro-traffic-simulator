/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author rgrunitzki
 */
public class IncrementalAssignmentOriginal extends Driver {

    private Route route;

    public IncrementalAssignmentOriginal(int id, Vertex origin, Vertex destination, Graph G) {
        super(id, origin, destination, G);
        this.resetAll();
    }

    @Override
    public void step_A() {
        current_road = route.getPath().get(Params.STEP - 1);
        current_intersection = current_road.getTo();
    }

    @Override
    public void step_B() {
//        this.traveledRoute.add(new RouteEntry(current_road, current_road.getCost()));
//        this.travelTime += current_road.getCost();
        this.traveledRoute.add(new RouteEntry(current_road, current_road.getCost()));
        this.travelTime += current_road.getCost();

        if (hasArrived()) {
            this.current_road = null;
        }
    }

    @Override
    public void beforeEpisode() {
        if (this.route == null && this.mustBeProcessed()) {
            ShortestPath shortestPath = new ShortestPath();
//            Map<String, EdgeEntry> edgesMap = shortestPath.listEdgeToHashtable(this.graph.getEdges(), false);

            Map<String, EdgeEntry> edgesMap;
            edgesMap = new Hashtable<>();
            for (Edge e : this.graph.getEdges()) {
                edgesMap.put(e.toString(), new EdgeEntry(e, e.getCost()));
            }

            List<Edge> path = shortestPath.dijkstra(this.graph.getVertex(), edgesMap, this.origin, this.destination);

            route = new Route(path, shortestPath.calcCostPath(edgesMap, path));
        }

    }

    @Override
    public void afterEpisode() {
    }

    //verifies if driver needs be on the simulation
    @Override
    public boolean mustBeProcessed() {
        float currentLoad = 0;
        for (int i = 1; i <= Params.EPISODE; i++) {
            currentLoad += Params.INC_FACTOR[i - 1] * Params.DEMAND_SIZE;
        }
        return this.id <= currentLoad;
    }
//    
//       @Override
//    public float getTravelTime() {
//        float cost = 0f;
//        for (Object entry: this.traveledRoute){
//            cost+= ((RouteEntry) entry).getEdge().getAcumulatedCost();
//        }
//        this.travelTime = cost;
//        return cost; //To change body of generated methods, choose Tools | Templates.
//    }

}


 
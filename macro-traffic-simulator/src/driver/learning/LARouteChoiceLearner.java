package driver.learning;

import driver.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import network.Edge;
import driver.EdgeEntry;
import network.Graph;
import driver.Route;
import driver.RouteEntry;
import network.Vertex;
import simulation.Params;
import util.KRoutes;
import util.ShortestPath;

@SuppressWarnings("rawtypes")
public class LARouteChoiceLearner extends Driver {

    private HashMap<String, EdgeEntry> edges;
    public ArrayList<Route> routes;
    private Route currentRoute;

    public LARouteChoiceLearner(int id, Vertex origin, Vertex destination, Graph G) {
        super(id, origin, destination, G);
        this.resetAll();
    }

    @Override
    public void beforeEpisode() {

        //Rerun K Shortest Paths with probability Params.AL_P_K_SP
        if (random.nextFloat() < Params.AL_P_K_SP) {
            routes.clear();
            for (Edge e : this.graph.getEdges()) {
                if (edges.get(e.toString()).getCost() < 0) {
                    edges.get(e.toString()).setCost(edges.get(e.toString()).getCost() * -1);//EXPLICAR O PORQUÊ DESSE ESQUEMA FUNCIONAR
                }
            }
            ShortestPath sp = new ShortestPath();
            for (List<Edge> route : sp.kShortestPaths(this.graph.getVertex(), this.edges, origin, destination, Params.K_ROUTES)) {
                routes.add(new Route(route, sp.calcCostPath(edges, route)));
            }
        }

        if (random.nextFloat() < Params.AL_P_DIJKSTRA) {
            //create new route
            ShortestPath sp = new ShortestPath();
            List<Edge> raux = sp.dijkstra(this.graph.getVertex(), this.edges, origin, destination);
            Route r = new Route(raux, sp.calcCostPath(edges, raux));
            //check if it exists
            boolean exists = false;
            for (Route rr : routes) {
                if (rr.equals(r)) {
                    exists = true;
                }
            }
            //store the new route into the list
            if (!exists) {
                //store
                routes.add(r);

                //update its reward according to the number of routes
                r.setCost(1 / (routes.size() + 1));
                float sum = 1 + r.getCost();//está diferente de quando cria as rotas (onde é usado o reward mesmo)

                //normalize all rewards to sum up to 1
                for (Route rr : routes) {
                    rr.setCost(rr.getCost() / sum);
                }

                sortRoutes();
            }
        }

        this.currentRoute = chooseRoute();
    }

    //Roulette wheel
    private Route chooseRoute() {

        //Select an action based on the probability vector
        //in the first episode, acts greedily
        if (Params.EPISODE == 1) {
            Route r = null;
            for (Route rr : routes) {
                if (r == null || rr.getCost() < r.getCost()) {
                    r = rr;
                }
            }
            return r;
        }

        //sum all rewards
        double sum = 0;
        for (Route r : routes) {
            sum += 1 / r.getCost();//the higher the reward, the lower the probability of being selected (the reward here is like the cost)
        }

        //draw a random action from the probability vector
        double rand = random.nextDouble() * sum;
        for (Route r : routes) {
            if (rand <= 1 / r.getCost()) {
                return r;
            } else {
                rand -= 1 / r.getCost();
            }
        }

        //if no action was selected, then draws one uniformely at random
        return routes.get(random.nextInt(routes.size()));/**/
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterEpisode() {

        updateModel();
        updateRoutes();
    }

    @SuppressWarnings("unchecked")
    private void updateModel() {
        //update the historic rewards of each road based on what was experimented in the current episode
        for (Object traveledRoute1 : traveledRoute) {
            RouteEntry re = (RouteEntry) traveledRoute1;
            edges.get(re.getEdge().toString()).setCost(re.getCost());
        }
        
    }

    private void updateRoutes() {

        //calculate current reward (adapted to reward inaction)
        float aux = (this.currentRoute.getCost() < 0) ? 1 : -1;
        float rt = (aux * currentRoute.getCost() + Params.AL_INACTION_SUM_FACTOR) / Params.AL_INACTION_SUM_FACTOR;

        for (Route r : routes) {
            //reset routes' reward in the first iteration (this cannot be done before first episode
            //because, in the first episode, the route choice is greedy according to the free flow travel time)
                      //chosen-route's rule
            if (r.equals(currentRoute)) {
                r.setCost(r.getCost() + Params.AL_INACTION_ALPHA * rt * (1 - r.getCost()));
            } //non-chosen-routes' rule
            else {
                r.setCost(r.getCost() - Params.AL_INACTION_ALPHA * rt * (r.getCost()));
            }
        }/**/

        sortRoutes();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void step_A() {

        //With small probability Params.AL_P_DEV_ROUTE, deviates from currentRoute 
        //by selecting a random road (from current intersection) and replaning the rest of the way
        if (Params.EPISODE < Params.NUM_EPISODES - 5 && random.nextFloat() < Params.AL_P_DEV_ROUTE) {
            deviateRoute();
        }

        if (this.current_road == null) {
            this.current_road = currentRoute.getPath().get(0);
        } else {
            int i = currentRoute.getPath().indexOf(this.current_road);
            this.current_road = currentRoute.getPath().get(i + 1);
        }
        this.current_intersection = this.current_road.getTo();
    }

    private void deviateRoute() {
        if (this.current_intersection.getOutEdges().size() > 1) {
            int id_new = random.nextInt(this.current_intersection.getOutEdges().size());
            int id_prev = currentRoute.getPath().indexOf(this.current_road);
            int id_old = this.current_intersection.getOutEdges().indexOf(currentRoute.getPath().get(id_prev + 1));
            if (id_new == id_old)//if (this.current_intersection.getOutEdges().get(id_new).equals(currentRoute.path.get(0)))
            {
                id_new = (id_new == 0) ? id_new++ : id_new--;
            }
            Edge new_edge = this.current_intersection.getOutEdges().get(id_new);
            ShortestPath sp = new ShortestPath();

            List<Edge> new_path = sp.dijkstra(this.graph.getVertex(), this.edges, new_edge.getTo(), this.destination);
            new_path.add(0, new_edge);
            int aux = 0;
            if (this.current_road != null) {
                for (Edge e : currentRoute.getPath().subList(0, currentRoute.getPath().indexOf(this.current_road) + 1)) {
                    new_path.add(aux++, e);
                }
            }

            currentRoute = new Route(new_path, sp.calcCostPath(this.edges, new_path));
            
            if (!containsRoute(currentRoute)) {
                routes.add(currentRoute);
            }

        }
    }

    private boolean containsRoute(Route r) {
        for (Route ri : routes) {
            if (ri.equals(r)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void step_B() {
        this.traveledRoute.add(new RouteEntry(this.current_road, this.current_road.getAcumulatedCost())); //HERE
        this.travelTime += this.current_road.getAcumulatedCost();//HERE

        //[DO NOT REMOVE] The number of vehicles in a given edge is computed before step_B. 
        //Only vehicles that traveled through a road in the CURRENT step are taken into
        //consideration for this purpose.
        if (hasArrived()) {
            this.current_road = null;
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void resetAll() {
        super.resetAll();

        this.routes = new ArrayList<>();
        this.currentRoute = null;

        this.edges = new HashMap<>();
        for (Edge e : this.graph.getEdges()) {
            edges.put(e.toString(), new EdgeEntry(e, e.getFreeFlowCost()));
        }
        
        for (Route r : KRoutes.getKRoutesOD(origin, destination, this.graph)) {
            routes.add(new Route(r.getPath(), r.getCost()));
        }
    }

    private void sortRoutes() {
        //sort the routes list according to reward
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route r1, Route r2) {
                double v1 = r1.getCost();
                double v2 = r2.getCost();
                return v1 < v2 ? -1 : (v1 > v2 ? 1 : 0);
            }
        });
    }
    
    

}
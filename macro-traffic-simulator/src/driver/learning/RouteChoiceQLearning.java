package driver.learning;

import util.KRoutes;
import driver.Driver;
import driver.Route;
import driver.RouteEntry;
import java.util.ArrayList;
import java.util.Random;

import network.Graph;
import network.Vertex;
import simulation.Params;

@SuppressWarnings("rawtypes")
public class RouteChoiceQLearning extends Driver {

    //in this problem (traveledRoute choice) we use a stateless MDP (just one state), so Vertex type is not important in QTable
    private StatelessQTable<Route> qtable;

    private Route currentRoute;//current action

    public ArrayList<Route> routes;

    public RewardFunction costFunction = new RouteLearningReward();

    public RouteChoiceQLearning(int id, Vertex origin, Vertex destination, Graph G) {
        super(id, origin, destination, G);
        resetAll();
    }

    @Override
    public void reset() {
        super.reset();
        this.currentRoute = null;
        this.travelTime = 0;
    }

    @Override
    public void resetAll() {
        super.resetAll();

        this.routes = new ArrayList<>();
        for (Route r : KRoutes.getKRoutesOD(this.origin, this.destination, this.graph)) {
            routes.add(new Route(r.getPath(), r.getCost()));
//            System.out.println(r.getPath().toString() + "\tCost: " + r.getCost()); apenas para imprimir as rotas conforme a ana pediu
        }

        this.currentRoute = null;

        this.qtable = new StatelessQTable<>(routes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void step_A() {
        current_road = currentRoute.getPath().get(Params.STEP - 1);
        current_intersection = current_road.getTo();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void step_B() {
        this.traveledRoute.add(new RouteEntry(current_road, current_road.getCost()));
        this.travelTime += current_road.getCost();

        //[DO NOT REMOVE] The number of vehicles in a given edge is computed before step_B. 
        //Only vehicles that traveled through a road in the CURRENT step are taken into
        //consideration for this purpose.
        if (hasArrived()) {
            this.current_road = null;
        }
    }

    private Route epsilonGreedy() {

        if (random.nextFloat() < Params.EG_EPSILON) {
            return chooseActionRandomly();
        } else {
            return chooseActionGreedily();
        }

    }

    private Route chooseActionRandomly() {
        return routes.get(random.nextInt(routes.size()));
    }

    private Route chooseActionGreedily() {
        //select the best action. if there are "multiple best" actions, 
        //select among them with equal probability. 
        //reservoir sampling is used to decrease the complexity of this procedure.
        Random r = new Random(Params.RANDOM.nextLong());
        Route choosen_a = routes.get(0);
        float best = Float.NEGATIVE_INFINITY;
        int counteq = 0;
        for (Route a : routes) {
            float thisv = qtable.getQValue(a);
            if (thisv > best) {
                best = thisv;
                counteq = 0;
            }
            if (thisv == best && r.nextFloat() < 1.0 / ++counteq) {
                choosen_a = a;
            }
        }
        return choosen_a;/**/

        /*//select the best action. if there "multiple best" actions, select the first in the list.
         Route choosen_a = routes.get(0);
         for (Route a : routes) {
         if (qtable.getQValue(a) > qtable.getQValue(choosen_a)) {
         choosen_a = a;
         }
         }
         return choosen_a;*/

    }

    /**
     * Update Q-Table according to the equation Q(a) = Q(a) + alpha * (r -
     * Q(a)).
     */
    private void updateQTable() {

        float qa = qtable.getQValue(currentRoute);
        float r = this.costFunction.eval(this);

        float value = qa + Params.QL_ALPHA * (r - qa);

        qtable.setQValue(currentRoute, value);

    }

    @Override
    public String toString() {
        return "QLrc" + super.toString();
    }

    public void printQTable() {
        qtable.print();
    }

    @Override
    public void beforeEpisode() {
        if (Params.EPISODE == 1) {
            currentRoute = routes.get(0);
        } else {
            currentRoute = epsilonGreedy();
        }
    }

    @Override
    public void afterEpisode() {
        updateQTable();
    }
    
     @Override
    public float getTravelTime() {
        float cost = 0f;
        for (Object entry : this.traveledRoute) {
            cost += ((RouteEntry) entry).getEdge().getAcumulatedCost();
        }
//        this.travelTime = cost;
        return cost; //To change body of generated methods, choose Tools | Templates.
    }
}

package driver.learning;

import driver.Driver;
import driver.RouteEntry;
import network.Edge;
import network.Graph;
import network.Vertex;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class IndividualQLearning extends Driver {

    //previous state
    private Vertex prev_intersection;
    private QTable qtable;
    private RewardFunction rewardFunction = new IndividualReward();

    public IndividualQLearning(int id, Vertex origin, Vertex destination, Graph G) {
        super(id, origin, destination, G);
        resetAll();

    }

    @Override
    public void reset() {
        super.reset();
        prev_intersection = null;
    }

    @Override
    public void resetAll() {
        super.resetAll();
        qtable = new QTable(graph.getVertex());
    }

    @Override
    public void step_A() {
        current_road = epsilonGreedy();
        takeAction(current_road);
        //System.out.println(this + ": I'm at " + current_intersection);
    }

    @Override
    public void step_B() {
        traveledRoute.add(new RouteEntry(current_road, current_road.getCost()));
        updateQTable();

        if (hasArrived()) {
            this.current_road = null;
        }
    }

    private Edge epsilonGreedy() {
        if (random.nextFloat() < Params.EG_EPSILON) {
            return chooseActionRandomly();
        } else {
            return chooseActionGreedily();
        }

    }

    private Edge chooseActionRandomly() {
        return current_intersection.getOutEdges().get(random.nextInt(current_intersection.getOutEdges().size()));
    }

    private Edge chooseActionGreedily() {
        Edge choosen_a = current_intersection.getOutEdges().get(0);
        for (Edge a : current_intersection.getOutEdges()) {
            if (qtable.getQValue(current_intersection, a) > qtable.getQValue(current_intersection, choosen_a)) {
                choosen_a = a;
            }
        }
        return choosen_a;
    }

    private void takeAction(Edge action) {

        prev_intersection = current_intersection;
        current_intersection = action.getTo();

    }

    /**
     * Update Q-Table according to the equation Q(s,a) = Q(s,a) + alpha * (r +
     * gamma * max_a'(Q(s',a')) - Q(s,a).
     */
    private void updateQTable() {

        float qsa = qtable.getQValue(prev_intersection, current_road);
        float r = 0f;

        r = this.rewardFunction.eval(this);
        float max_qsa_new = qtable.getQValue(current_intersection, current_intersection.getOutEdges().get(0));

        for (Edge e : current_intersection.getOutEdges()) {
            if (qtable.getQValue(current_intersection, e) > max_qsa_new) {
                max_qsa_new = qtable.getQValue(current_intersection, e);
            }
        }
        qsa = (1 - Params.QL_ALPHA) * qsa + Params.QL_ALPHA * (r + Params.QL_GAMMA * max_qsa_new);
        qtable.setQValue(prev_intersection, current_road, qsa);
    }

    @Override
    public String toString() {
        return "QL" + super.toString();
    }

    public void printQTable() {
        qtable.print();
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
        for (Object entry : this.traveledRoute) {
            cost += ((RouteEntry) entry).getEdge().getAcumulatedCost();
        }
//        this.travelTime = cost;
        return cost; //To change body of generated methods, choose Tools | Templates.
    }
}

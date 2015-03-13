package network;

import simulation.Params;

/**
 * An edge structure to represent the network's road.
 *
 * @author Ricardo Grunitzki
 */
public class Edge implements Comparable<Edge> {

    /**
     * integer identifier.
     */
    private String id;
    /**
     * boolean flag indicating if is directed or no.
     */
    private boolean undirected;
    /**
     * vertex from.
     */
    private Vertex from;
    /**
     * vertex to.
     */
    private Vertex to;
    /**
     * float value representing the capacity.
     */
    private float capacity;
    /**
     * float value representing the free flow cost of the edge.
     */
    private float freeFlowCost;
    /**
     * float value representing alpha parameter on cost function.
     */
    private float alpha;
    /**
     * float value representing beta parameter on cost function.
     */
    private float beta;
    /**
     * integer value representing the accumulated flow in edge over the episode.
     */
    private int totalFlow;
    /**
     * integer value representing the current number of vehicles over the edge.
     */
    private int vehiclesHere;

    /**
     * float value representing method of successive averages flow.
     */
    private float msaFlow;

    /**
     * float value representing the current travel time on link.
     */
    private double cost;

    /**
     * costFunction object for calculating the road's cost.
     */
    private CostFunction costFunction;

    /**
     * Sample Edge constructor.
     */
    public Edge() {

    }

    /**
     *
     * Edge constructor
     *
     * @param id edge identifier
     * @param from origin Vertex
     * @param to destination Vertex
     * @param capacity cost function capacity
     * @param directed flag indicating if edge is directed
     * @param freeflow edge free flow travel cost
     * @param alpha cost function parameter
     * @param beta cost function parameter
     * @param costFunction cost function object
     */
    public Edge(String id, Vertex from, Vertex to, float capacity, boolean directed,
            float freeflow, float alpha, float beta, CostFunction costFunction) {
        this.id = id;
        this.undirected = !directed;

        this.from = from;
        this.from.addOutEdge(this);
        this.to = to;
        this.to.addInEdge(this);

        if (this.undirected) {
            this.from.addInEdge(this);
            this.to.addOutEdge(this);
        }

        this.vehiclesHere = 0;

        this.totalFlow = 0;
        this.msaFlow = 0;

        this.capacity = capacity;
        this.cost = freeflow;
        this.freeFlowCost = freeflow;
        this.alpha = alpha;
        this.beta = beta;
        this.costFunction = costFunction;
    }

    /**
     * Method for updating Edge's cost.
     */
    public void updateCost() {
        this.cost = this.costFunction.evalCost(this, this.vehiclesHere);
    }

    /**
     * method used to get the cost based on the accumulated flow.
     *
     * @return a float value representing the cost function for the accumulated flow.
     */
    public float getAcumulatedCost() {
        return this.costFunction.evalCost(this, this.totalFlow);
    }

    @Override
    public String toString() {
        return from + ((undirected) ? " -- " : " -- ") + to;
    }

    /**
     * increment the amount of vehicles on edge.
     */
    public void incVehiclesHere() {
        this.vehiclesHere++;
        this.totalFlow++;
    }

    /**
     * clear the number of vehicles on edge.
     */
    public void clearVehiclesHere() {
        this.vehiclesHere = 0;
    }

    /**
     * clear the total flow count.
     */
    public void clearTotalFlow() {
        this.totalFlow = 0;
    }

//    @Override
//    public int compareTo(Edge edge2) {
//        return this.toString().compareTo(edge2.toString());
//    }
    /**
     * this method updates <code>msaFlow</code> attribute based on method of
     * successive averages rules. The update rule used is defined on
     * Transportation Modeling book on page 392. Maybe in the future the
     * <code>Params.ETA</code> could be variable along the episodes.
     */
    public void updateMSAFlow() {
//        this.msaFlow = this.msaFlow + Params.PHI_MSA * (-this.msaFlow + this.totalFlow);
        this.msaFlow = (1-Params.PHI_MSA)*this.msaFlow + Params.PHI_MSA *(this.totalFlow);
    }

    public float msaCost() {
//        if (Params.EPISODE == 1) {
//            return costFunction.evalCost(this, this.vehiclesHere);
//        } else {
        return costFunction.evalCost(this, this.msaFlow);
//        }
    }

    public void afterEpisode() {
        this.updateMSAFlow();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public boolean isUndirected() {
//        return undirected;
//    }
//
//    public void setUndirected(boolean undirected) {
//        this.undirected = undirected;
//    }
    public Vertex getFrom() {
        return from;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public Vertex getTo() {
        return to;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public float getFreeFlowCost() {
        return freeFlowCost;
    }

    public void setFreeFlowCost(float freeFlowCost) {
        this.freeFlowCost = freeFlowCost;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public int getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(int totalFlow) {
        this.totalFlow = totalFlow;
    }

    public int getVehiclesHere() {
        return vehiclesHere;
    }

    public void setVehiclesHere(int vehiclesHere) {
        this.vehiclesHere = vehiclesHere;
    }

    public float getMsaFlow() {
        return msaFlow;
    }

    public void setMsaFlow(float msaFlow) {
        this.msaFlow = msaFlow;
    }

    public float getCost() {
        return (float)cost;
    }
    public double getCostDouble() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public CostFunction getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(CostFunction costFunction) {
        this.costFunction = costFunction;
    }

    @Override
    public int compareTo(Edge o) {
        try {
            return Float.compare(new Float(this.id), new Float(o.id));
        }catch (Exception e){
            return this.id.compareTo(o.id);
        }
    }
}

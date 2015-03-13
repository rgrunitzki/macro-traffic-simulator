package network;

/**
 *
 * @author Ricardo Grunitzki
 */
public abstract class CostFunction {

    public abstract float evalCost(Edge edge, float desirable_flow);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 *
 * @author rgrunitzki
 */
public class BypassCostFunction extends CostFunction{

    @Override
    public float evalCost(Edge edge, float desirable_flow) {
        return edge.getFreeFlowCost() + desirable_flow * edge.getBeta();
    }
    
}

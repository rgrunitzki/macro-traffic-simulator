/*
 * To change edge license header, choose License Headers in Project Properties.
 * To change edge template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change edge license header, choose License Headers in Project Properties.
 * To change edge template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

/**
 *
 * @author rgrunitzki
 */
public class VolumeDelayFunction extends CostFunction {

    @Override
    public float evalCost(Edge edge, float desirable_flow) {
        return  edge.getFreeFlowCost() * (1 + edge.getAlpha()* (float) Math.pow(desirable_flow / edge.getCapacity(), edge.getBeta()));
    }
}

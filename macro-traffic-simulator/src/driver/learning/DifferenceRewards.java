/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import network.Edge;

/**
 *
 * @author rgrunitzki
 */
public class DifferenceRewards extends RewardFunction {

    @Override
    public float eval(Driver driver) {
        Edge e = new Edge();
        e.setCostFunction(driver.getCurrent_road().getCostFunction());
        e.setFreeFlowCost(driver.getCurrent_road().getFreeFlowCost());
        e.setVehiclesHere(driver.getCurrent_road().getVehiclesHere() - 1);
        e.setAlpha(driver.getCurrent_road().getAlpha());
        e.setBeta(driver.getCurrent_road().getBeta());
        e.setCapacity(driver.getCurrent_road().getCapacity());
        e.updateCost();

        double gz = driver.getCurrent_road().getVehiclesHere() * driver.getCurrent_road().getCostDouble();
        double gz_i = (e.getVehiclesHere()) * e.getCostDouble();
        double reward = gz - gz_i;

//        if (driver.getId() == 22589) {
//            System.out.println("tt:" + driver.getCurrent_road().getCost() + "\tdtt:" + e.getCost() + 
//                               "\ttt-dtt:" + (driver.getCurrent_road().getCost() - e.getCost())+
//                               "\tg(z): " + gz + "\tg(z-z_i): " + gz_i + "\tg(z)-g(z-z_i): " + reward);
//
//        }
        return -1.f*(float)reward;
    }

}

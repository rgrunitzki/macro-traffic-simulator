/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package driver.learning;

import driver.Driver;

/**
 *
 * @author rgrunitzki
 */
public class IndividualReward extends RewardFunction{

    @Override
    public float eval(Driver driver) {
        return -1.0f*(driver.getCurrent_road().getAcumulatedCost());
    }
    
}

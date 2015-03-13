package driver.learning;

import driver.Driver;

/**
 *
 * @author Ricardo Grunitzki
 */
public class RouteLearningReward extends RewardFunction {

    @Override
    public float eval(Driver driver) {
        return -driver.getTravelTime();
    }

}

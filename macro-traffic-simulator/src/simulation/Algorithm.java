/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

/**
 *
 * @author rgrunitzki
 */
public enum Algorithm {

    IQLearning("IQ-learning"),
    DQLearning("DQ-learning"),
    QLRouteChoice("Q-learningRouteChoice"),
    AllOrNothing("All-Or-Nothing"),
    IncrementalAssignment("IncrementalAssingment"),
    SuccessiveAverages("SuccessiveAverages"),
    LearningAutomata("LearningAutomata");

    private final String value;

    private Algorithm(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

package com.numberGenerator.model;

public class NumberGen {

    private String goal;
    private String step;

    public NumberGen() {}

    public NumberGen(String goal, String step) {
        this.goal = goal;
        this.step = step;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}

package com.numberGenerator.model;

import java.util.List;

public class Result {

    private List<Integer> numResult;

    public Result(List<Integer> numResult) {
        this.numResult = numResult;
    }

    public List<Integer> getNumResult() {
        return numResult;
    }

    public void setNumResult(List<Integer> numResult) {
        this.numResult = numResult;
    }
}

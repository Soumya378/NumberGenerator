package com.numberGenerator.model;

import java.util.List;

public class BulkGenerate {

    private List<String> results;

    public BulkGenerate(List<String> results) {
        this.results = results;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }
}

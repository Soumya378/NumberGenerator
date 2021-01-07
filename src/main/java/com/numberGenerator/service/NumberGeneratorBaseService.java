package com.numberGenerator.service;

import com.numberGenerator.model.Result;

import java.util.concurrent.Future;

public interface NumberGeneratorBaseService {

    Future<Result> create(String goal, String step) throws InterruptedException;
}

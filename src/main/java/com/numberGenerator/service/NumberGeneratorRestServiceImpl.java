package com.numberGenerator.service;

import com.numberGenerator.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class NumberGeneratorRestServiceImpl implements NumberGeneratorBaseService {

    @Value("${randomTime.minTime}")
    private int minTime;

    @Value("${randomTime.maxTime}")
    private int maxTime;

    @Override
    @Async
    public Future<Result> create(String goal, String step) throws InterruptedException {

        List<Integer> list = new ArrayList<Integer>();
        list.add(Integer.valueOf(goal));

        int goalValue = Integer.valueOf(goal);
        int stepValue = Integer.valueOf(step);

        while(goalValue>=0)
        {
            int random_int = (int)(Math.random() * (maxTime - minTime + 1) + minTime);

            TimeUnit.SECONDS.sleep(random_int);

            goalValue -= stepValue;

            if(goalValue>=0)
                list.add(goalValue);
        }

        Result rs = new Result(list);
        return new AsyncResult<>(rs);
    }
}

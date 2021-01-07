package com.numberGenerator.controller;

import com.numberGenerator.model.*;
import com.numberGenerator.service.NumberGeneratorBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
@EnableAsync
public class NumGenController {

    @Autowired
    private NumberGeneratorBaseService restService;

    private HashMap<String, List<Future<Result>>> future = new HashMap<>();

    @GetMapping("/tasks/{uuid}/status")
    public  DeferredResult<ResponseEntity<?>> getStatus(@PathVariable("uuid") String uuid) throws InterruptedException {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        boolean flag = true;
        try {
            for(int i = 0;i<future.get(uuid).size();i++) {
                if(!future.get(uuid).get(i).isDone()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                UUIDGen ug = new UUIDGen("SUCCESS");
                ResponseEntity<UUIDGen> responseEntity = new ResponseEntity<>(ug, HttpStatus.CREATED);
                deferredResult.setResult(responseEntity);
            } else {
                UUIDGen ug = new UUIDGen("IN_PROGRESS");
                ResponseEntity<UUIDGen> responseEntity = new ResponseEntity<>(ug, HttpStatus.OK);
                deferredResult.setResult(responseEntity);
            }
        }catch(Exception e)
        {
            UUIDGen ug = new UUIDGen("ERROR");
            ResponseEntity<UUIDGen> responseEntity = new ResponseEntity<>(ug, HttpStatus.INTERNAL_SERVER_ERROR);
            deferredResult.setResult(responseEntity);
        }
        return deferredResult;
    }

    @GetMapping(value = "/tasks/{uuid}")
    public DeferredResult<ResponseEntity<?>> getResult(@PathVariable("uuid") String uuid, @RequestParam("action") String action) throws InterruptedException {

        //System.out.println(uuid+" "+action+""+future);
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();

        if(!action.equals("get_numlist"))
            return deferredResult;

        try {
            if (future.get(uuid).size() != 0) {
                if(future.get(uuid).size() == 1) {
                    Future<Result> futureResult = future.get(uuid).get(0);
                    Result rs = futureResult.get();
                    String result = rs.getNumResult().stream()
                            .map(n -> String.valueOf(n))
                            .collect(Collectors.joining(","));
                    UUIDGen ug = new UUIDGen(result);
                    deferredResult.setResult(new ResponseEntity<>(ug, HttpStatus.OK));
                }
                else {
                    List<String> listBulk = new ArrayList<>();
                    for(int i=0;i<future.get(uuid).size();i++){
                        Future<Result> futureResult = future.get(uuid).get(i);
                        Result rs = futureResult.get();
                        String result = rs.getNumResult().stream()
                                .map(n -> String.valueOf(n))
                                .collect(Collectors.joining(","));
                        listBulk.add(result);
                    }
                    BulkGenerate ug = new BulkGenerate(listBulk);
                    deferredResult.setResult(new ResponseEntity<>(ug, HttpStatus.OK));
                }
            } else {
                deferredResult.setResult(new ResponseEntity<>(new UUIDGen(""), HttpStatus.OK));
                return deferredResult;
            }
        }catch(Exception e)
        {
            deferredResult.setResult(new ResponseEntity<>(new UUIDGen(""), HttpStatus.OK));
            return deferredResult;
        }
        return deferredResult;
    }

    @PostMapping("/generate")
    public  DeferredResult<ResponseEntity<?>> getId(@RequestBody NumberGen numberGen) throws InterruptedException {
        List<Future<Result>> list = new ArrayList<>();
        String uuid = UUID.randomUUID().toString();

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        Future<Result> result = restService.create(numberGen.getGoal(), numberGen.getStep());

       // System.out.println("after future");
        list.add(result);
        future.put(uuid, list);
        /*PendingResult pr = new PendingResult(
                uuid
                , "In Progress, please consult on the referenced URL"
                , "GET - /api/tasks/"+uuid+"?action=get_numlist");*/
        TaskGen ug = new TaskGen(uuid);
        ResponseEntity<TaskGen> responseEntity = new ResponseEntity<>(ug, HttpStatus.ACCEPTED);                   // returns pendingObject indicating how to poll the status
        deferredResult.setResult(responseEntity);

        return deferredResult;
    }

    @PostMapping("/bulkGenerate")
    public DeferredResult<ResponseEntity<?>> getBulkId(@RequestBody List<NumberGen> bulkGenerate) throws InterruptedException{

        String uuid = UUID.randomUUID().toString();

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();

        List<Future<Result>> listBulk = new ArrayList<>();
        for(int i =0;i<bulkGenerate.size();i++) {
            NumberGen numberGen = bulkGenerate.get(i);
            Future<Result> result = restService.create(numberGen.getGoal(), numberGen.getStep());
            listBulk.add(result);
        }
        future.put(uuid, listBulk);

        TaskGen ug = new TaskGen(uuid);
        ResponseEntity<TaskGen> responseEntity = new ResponseEntity<>(ug, HttpStatus.ACCEPTED);                   // returns pendingObject indicating how to poll the status
        deferredResult.setResult(responseEntity);

        return deferredResult;
    }

    /*
    @PostMapping("/bulkGenerateFaster")
    public DeferredResult<ResponseEntity<?>> getBulkIdAlternate(@RequestBody List<NumberGen> bulkGenerate) throws InterruptedException{

        String uuid = UUID.randomUUID().toString();

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();

        //List<Future<Result>> listBulk = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList();

        for(int i =0;i<bulkGenerate.size();i++) {
            NumberGen numberGen = bulkGenerate.get(i);
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    restService.create(numberGen.getGoal(), numberGen.getStep());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));

            //Future<Result> result = restService.create(numberGen.getGoal(), numberGen.getStep());
           // listBulk.add(result);
        }
        List<Future<Result>> listBulk = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        future.put(uuid, listBulk);

        TaskGen ug = new TaskGen(uuid);
        ResponseEntity<TaskGen> responseEntity = new ResponseEntity<>(ug, HttpStatus.ACCEPTED);                   // returns pendingObject indicating how to poll the status
        deferredResult.setResult(responseEntity);

        return deferredResult;
    }
    */

}

package com.zoracorp.listener;

import com.zoracorp.model.BatchPercentage;
import com.zoracorp.repository.BatchPercentageRepo;
import com.zoracorp.service.UserService;
import lombok.SneakyThrows;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;


public class ItemCountListener implements ChunkListener {

    @Autowired
    private UserService userService;

    @Autowired
    private BatchPercentageRepo repo;

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @SneakyThrows
    @Override
    public void afterChunk(ChunkContext context) {
        int totalRecord = (int) context.getStepContext().getJobExecutionContext().get("totalRecord");
        int processedChunk = context.getStepContext().getStepExecution().getReadCount();
        int calculatePercentage = userService.calculatePercentage(totalRecord, processedChunk);
        System.out.println("Item Processed: " + processedChunk);
        System.out.println("Percentage: "+calculatePercentage);
        String batchId = String.valueOf(context.getStepContext().getJobParameters().get("time"));

        BatchPercentage percentage = new BatchPercentage();
        percentage.setBatchId(batchId);
        percentage.setPercentage(calculatePercentage);
        BatchPercentage findPercentage = repo.findByBatchId(batchId);
        if (findPercentage.getId()==0) {
            repo.add(percentage);
        } else {
            percentage.setId(findPercentage.getId());
            repo.update(percentage);
        }

    }

    @Override
    public void afterChunkError(ChunkContext context) {
    }



}
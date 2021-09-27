package com.zoracorp.listener;

import lombok.SneakyThrows;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;


@Component
public class JobListener extends JobExecutionListenerSupport {

    @Value("${file.path}")
    private String filePath;

    @SneakyThrows
    @Override
    public void beforeJob(JobExecution jobExecution){
        int totalRecord = (int) (Files.lines(Paths.get(filePath)).count() - 1);
        jobExecution.getExecutionContext().put("totalRecord", totalRecord);

        System.out.println("Total Record: "+ totalRecord);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    }
}

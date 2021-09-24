package com.zoracorp.listener;

import com.zoracorp.service.UserService;
import lombok.SneakyThrows;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class JobListener extends JobExecutionListenerSupport {
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    @Autowired
    public JobListener(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public void beforeJob(JobExecution jobExecution){
        int totalRecord = userService.count("records.csv");
        jobExecution.getExecutionContext().put("totalRecord", totalRecord);
        System.out.println("Total Record: "+ totalRecord);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    }
}

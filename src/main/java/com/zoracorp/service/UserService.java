package com.zoracorp.service;

import com.zoracorp.model.BatchPercentage;
import com.zoracorp.repository.BatchPercentageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private BatchPercentageRepo repo;

    public Integer calculatePercentage(int total, int processedValue) {
        return processedValue * 100 / total;
    }

    public Integer findPercentageByJobId(String jobId) {
        BatchPercentage percentage = repo.findByBatchId(jobId);
        return percentage.getPercentage();
    }
}

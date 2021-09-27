package com.zoracorp.service;

import com.zoracorp.model.BatchPercentage;
import com.zoracorp.repository.BatchPercentageRepo;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class UserService {

    @Autowired
    private BatchPercentageRepo repo;

    @Value("${file.path}")
    private String filePath;


    public Integer calculatePercentage(int total, int processedValue) {
        return processedValue * 100 / total;
    }

    public Integer findPercentageByJobId(String jobId) {
        BatchPercentage percentage = repo.findByBatchId(jobId);
        return percentage.getPercentage();
    }

    public JobParameters processFile(MultipartFile file) throws IOException {
        IOUtils.copy(file.getInputStream(), new FileOutputStream(filePath));
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis()).toJobParameters();
        System.out.println("JobID: "+ jobParameters.getParameters().get("time"));

        return jobParameters;

    }
}

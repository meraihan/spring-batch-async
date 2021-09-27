package com.zoracorp.controller;

import com.zoracorp.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;

@RestController
public class UserController {

    @Autowired JobLauncher jobLauncher;

    @Autowired Job job;

    @Autowired UserService userService;

    @Autowired TaskExecutor threadPoolTaskExecutor;

    @Value("${file.path}")
    private String filePath;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String batchId = "";
        if (file.isEmpty()) {
            return "Please select a file to upload.";
        }
        try {
            System.out.println("Start File upload process...");
            IOUtils.copy(file.getInputStream(), new FileOutputStream(filePath));
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
            System.out.println("JobID: "+ jobParameters.getParameters().get("time"));
            batchId = String.valueOf(jobParameters.getParameters().get("time"));
            threadPoolTaskExecutor.execute(() -> {
                try {
                    jobLauncher.run(job, jobParameters);
                } catch (JobExecutionAlreadyRunningException e) {
                    e.printStackTrace();
                } catch (JobRestartException e) {
                    e.printStackTrace();
                } catch (JobInstanceAlreadyCompleteException e) {
                    e.printStackTrace();
                } catch (JobParametersInvalidException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }
        return batchId;
    }


    @GetMapping("/find-percentage")
    public Integer search(@RequestParam("jobId") String jobId) {
        return userService.findPercentageByJobId(jobId);
    }

}

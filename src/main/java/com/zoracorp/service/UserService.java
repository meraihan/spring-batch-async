package com.zoracorp.service;

import com.zoracorp.model.BatchPercentage;
import com.zoracorp.repository.BatchPercentageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class UserService {


    @Autowired
    private BatchPercentageRepo repo;

    public int count(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    public Integer calculatePercentage(int total, int processedValue) {
        return processedValue * 100 / total;
    }


    public Integer findPercentageByJobId(String jobId) {
        BatchPercentage percentage = repo.findByBatchId(jobId);
        return percentage.getPercentage();
    }
}

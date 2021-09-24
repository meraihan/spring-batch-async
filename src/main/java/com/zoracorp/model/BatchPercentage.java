package com.zoracorp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BatchPercentage {
    private long id;
    private String batchId;
    private Integer percentage;
}

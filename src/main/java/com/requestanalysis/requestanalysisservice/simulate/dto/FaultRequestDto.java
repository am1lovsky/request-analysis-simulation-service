package com.requestanalysis.requestanalysisservice.simulate.dto;

import lombok.Data;

@Data
public class FaultRequestDto {
    private Integer statusCode;
    private Long delay;
    private Boolean brokenJson;
    private Integer responseSize;
    private String baseMessage;
}

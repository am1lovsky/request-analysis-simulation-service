package com.requestanalysis.requestanalysisservice.simulate.dto;

import lombok.Data;

@Data
public class FaultRequestDto {
    private String httpMethod;
    private Integer statusCode;
    private Long delay;
    private Boolean brokenJson;
    private String body;
    private Integer responseSize;
    private String baseMessage;
}

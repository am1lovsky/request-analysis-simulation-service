package com.requestanalysis.requestanalysisservice.analysis.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RequestDetails {
    private String method;
    private String url;
    private Map<String, String> headers;
    private Map<String, String[]> queryParameters;
    private String body;
    private String clientIp;
}

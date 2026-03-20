package com.requestanalysis.requestanalysisservice.analysis.factory;

import com.requestanalysis.requestanalysisservice.analysis.model.RequestDetails;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestDetailsFactory {

    private RequestDetailsFactory() {
    }

    public static RequestDetails from(HttpServletRequest request, String body) {
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(header -> header, request::getHeader));

        return RequestDetails.builder()
                .method(request.getMethod())
                .url(request.getRequestURL().toString())
                .headers(headers)
                .queryParameters(request.getParameterMap())
                .body(body)
                .clientIp(request.getRemoteAddr())
                .build();
    }
}

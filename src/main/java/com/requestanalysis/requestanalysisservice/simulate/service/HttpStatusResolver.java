package com.requestanalysis.requestanalysisservice.simulate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class HttpStatusResolver {

    public HttpStatus resolve(int statusCode) {
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (IllegalArgumentException e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}

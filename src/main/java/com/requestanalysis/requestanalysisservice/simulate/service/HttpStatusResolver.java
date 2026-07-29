package com.requestanalysis.requestanalysisservice.simulate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpStatusResolver {

    public HttpStatus resolve(int statusCode) {
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (IllegalArgumentException e) {
            log.warn("Unrecognized HTTP status code: {}, falling back to 500", statusCode);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}

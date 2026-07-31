package com.requestanalysis.requestanalysisservice.simulate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpStatusResolver {

    private static final int MINIMUM_RESPONSE_SIZE = 100;
    private static final int MAXIMUM_RESPONSE_SIZE = 200;

    public HttpStatus resolve(int statusCode) {
        if (statusCode < MINIMUM_RESPONSE_SIZE || statusCode > MAXIMUM_RESPONSE_SIZE) {
            log.warn("Invalid HTTP status code: {}, falling back to 500", statusCode);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            log.warn("Unrecognized HTTP status code: {}, falling back to 500", statusCode);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return status;
    }
}

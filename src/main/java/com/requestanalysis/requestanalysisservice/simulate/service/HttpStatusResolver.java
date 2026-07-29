package com.requestanalysis.requestanalysisservice.simulate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpStatusResolver {

    public HttpStatus resolve(int statusCode) {
        if (statusCode < 100 || statusCode > 599) {
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

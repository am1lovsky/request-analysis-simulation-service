package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultResponseMeta;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SimulationResponseGenerator {

    private final String patternError;
    private final String patternMessage;

    public SimulationResponseGenerator(FaultSimulationServiceProperties properties) {
        this.patternError = properties.patternError();
        this.patternMessage = properties.patternMessage();
    }

    public SimulatedResponse generate(FaultRequestDto request) {
        int statusCode = resolveStatusCode(request);
        long delay = resolveDelay(request);
        boolean isJsonBroken = resolveBrokenJson(request);
        int responseSize = resolveResponseSize(request);
        String baseMessage = resolveBaseMessage(request);

        String body;
        if (request.getBody() != null && !request.getBody().isBlank()) {
            body = request.getBody();
        } else {
            body = buildBody(baseMessage, responseSize, isJsonBroken);
        }

        FaultResponseMeta meta = buildMeta(statusCode, delay, isJsonBroken, responseSize);
        return new SimulatedResponse(statusCode, body, meta);
    }

    private int resolveStatusCode(FaultRequestDto request) {
        return request.getStatusCode() != null ? request.getStatusCode() : 500;
    }

    private boolean resolveBrokenJson(FaultRequestDto request) {
        return Boolean.TRUE.equals(request.getBrokenJson());
    }

    private long resolveDelay(FaultRequestDto request) {
        return request.getDelay() != null ? request.getDelay() : 0;
    }

    private int resolveResponseSize(FaultRequestDto request) {
        return request.getResponseSize() != null ? request.getResponseSize() : 0;
    }

    private String resolveBaseMessage(FaultRequestDto request) {
        return request.getBaseMessage() != null ? request.getBaseMessage() : "Simulated Failure";
    }

    private FaultResponseMeta buildMeta(int statusCode, long delay, boolean isJsonBroken, int responseSize) {
        return FaultResponseMeta.builder()
                .statusCode(statusCode)
                .delay(delay)
                .isJsonBroken(isJsonBroken)
                .responseSize(responseSize)
                .timestamp(Instant.now())
                .build();
    }

    private String buildBody(String baseMessage, int responseSize, boolean isJsonBroken) {
        int bytes = 1024;
        int targetBytes = Math.max(responseSize, 1) * bytes;
        String pattern = isJsonBroken
                ? patternError + baseMessage + "\" "
                : patternMessage + baseMessage + "\"}";
        StringBuilder builder = new StringBuilder();
        while (builder.length() < targetBytes) {
            builder.append(pattern).append('\n');
        }
        return builder.toString();
    }
}

package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultResponseMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j // - simplify logging
@Service
public class FaultSimulationService {

    private final String patternError;
    private final String patternMessage;

    public FaultSimulationService(FaultSimulationServiceProperties properties) {
        this.patternError = properties.patternError();
        this.patternMessage = properties.patternMessage();
    }

    public SimulatedResponse simulate(FaultRequestDto request) {
        int statusCode = resolveStatusCode(request);
        long delay = resolveDelay(request);
        boolean isJsonBroken = resolveBrokenJson(request);
        int responseSize = resolveResponseSize(request);
        String baseMessage = resolveBaseMessage(request);
        applyDelay(delay);
        String body = buildBody(baseMessage, responseSize, isJsonBroken);
        FaultResponseMeta meta = buildMeta(statusCode, delay, isJsonBroken, responseSize);
        log.info("Simulated fault meta: {}", meta);
        return new SimulatedResponse(statusCode, body, meta);
    }

    private int resolveStatusCode(FaultRequestDto request) {
        return request.getStatusCode() != null
                ? request.getStatusCode()
                : 500;
    }

    private boolean resolveBrokenJson(FaultRequestDto request) {
        return Boolean.TRUE.equals(request.getBrokenJson());
    }

    private long resolveDelay(FaultRequestDto request) {
        return request.getDelay() != null
                ? request.getDelay()
                : 0;
    }

    private int resolveResponseSize(FaultRequestDto request) {
        return request.getResponseSize() != null
                ? request.getResponseSize()
                : 0;
    }

    private String resolveBaseMessage(FaultRequestDto request) {
        return request.getBaseMessage() != null
                ? request.getBaseMessage()
                : "Simulated Failure";
    }

    private void applyDelay(long delay) {
        if (delay <= 0) {
            return;
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting for delay", e);
        }
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
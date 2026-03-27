package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.controller.DebugResponse;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class FaultSimulationService {

    private final SimulationConfigurationService configurationService;
    private final SimulationResponseGenerator responseGenerator;
    private final SimulationHistoryService historyService;

    public FaultSimulationService(SimulationConfigurationService configurationService,
                                  SimulationResponseGenerator responseGenerator,
                                  SimulationHistoryService historyService) {
        this.configurationService = configurationService;
        this.responseGenerator = responseGenerator;
        this.historyService = historyService;
    }

    public void configure(FaultRequestDto request) {
        configurationService.configure(request);
    }

    public ResponseEntity<Object> simulateAndBuildResponse(boolean isDebug, String httpMethod) {
        SimulatedResponse simulatedResponse = simulate(httpMethod);

        if (isDebug) {
            DebugResponse debugResponse = new DebugResponse(simulatedResponse.meta(), simulatedResponse.body());
            return ResponseEntity.ok(debugResponse);
        }

        HttpStatus status = resolveHttpStatus(simulatedResponse.statusCode());
        return ResponseEntity.status(status).body(simulatedResponse.body());
    }

    private HttpStatus resolveHttpStatus(int code) {
        try {
            return HttpStatus.valueOf(code);
        } catch (IllegalArgumentException e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public SimulatedResponse simulate(String httpMethod) {
        FaultRequestDto configuration = configurationService.getCurrentConfiguration();
        return simulate(Objects.requireNonNullElseGet(configuration, FaultRequestDto::new), httpMethod);
    }

    public SimulatedResponse simulate(FaultRequestDto request, String httpMethod) {
        long start = System.currentTimeMillis();

        SimulatedResponse response = responseGenerator.generate(request, httpMethod);

        applyDelay(response.meta().getDelay());

        log.info("Simulated fault meta: {}", response.meta());
        long executionTime = System.currentTimeMillis() - start;

        historyService.save(request, httpMethod, response.meta(), executionTime);

        return response;
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
}
package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.controller.DebugResponse;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.generator.SimulationResponseGenerator;
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
    private final HttpStatusResolver httpStatusResolver;
    private final SimulationDelayHandler delayHandler;

    public FaultSimulationService(SimulationConfigurationService configurationService,
                                  SimulationResponseGenerator responseGenerator,
                                  SimulationHistoryService historyService,
                                  HttpStatusResolver httpStatusResolver,
                                  SimulationDelayHandler delayHandler) {
        this.configurationService = configurationService;
        this.responseGenerator = responseGenerator;
        this.historyService = historyService;
        this.httpStatusResolver = httpStatusResolver;
        this.delayHandler = delayHandler;
    }

    public void configure(FaultRequestDto request) {
        configurationService.configure(request);
    }

    public ResponseEntity<Object> simulateAndBuildResponse(boolean isDebug, String httpMethod) {
        SimulatedResponse simulatedResponse = simulateCurrentConfiguration(httpMethod);

        if (isDebug) {
            DebugResponse debugResponse = new DebugResponse(simulatedResponse.meta(), simulatedResponse.body());
            return ResponseEntity.ok(debugResponse);
        }

        HttpStatus status = httpStatusResolver.resolve(simulatedResponse.statusCode());
        return ResponseEntity.status(status).body(simulatedResponse.body());
    }

    public SimulatedResponse simulateCurrentConfiguration(String httpMethod) {
        FaultRequestDto configuration = configurationService.getCurrentConfiguration();
        return simulate(Objects.requireNonNullElseGet(configuration, FaultRequestDto::new), httpMethod);
    }

    public SimulatedResponse simulate(FaultRequestDto request, String httpMethod) {
        long start = System.currentTimeMillis();

        SimulatedResponse response = responseGenerator.generate(request, httpMethod);

        delayHandler.applyDelay(response.meta().getDelay());

        log.info("Simulated fault meta: {}", response.meta());
        long executionTime = System.currentTimeMillis() - start;

        historyService.save(request, httpMethod, response.meta(), executionTime);

        return response;
    }

}
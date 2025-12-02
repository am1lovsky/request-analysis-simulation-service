package com.requestanalysis.requestanalysisservice.simulate.controller;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.service.FaultSimulationService;
import com.requestanalysis.requestanalysisservice.simulate.service.SimulatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaultSimulationController {

    private final FaultSimulationService faultSimulationService;

    public FaultSimulationController(FaultSimulationService faultSimulationService) {
        this.faultSimulationService = faultSimulationService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<Object> simulate(@RequestBody FaultRequestDto request,
                                           @RequestParam(name = "isDebug", required = false, defaultValue = "false") boolean isDebug) {

        SimulatedResponse simulatedResponse = faultSimulationService.simulate(request);
        HttpStatus status = resolveHttpStatus(simulatedResponse);
        return buildResponse(simulatedResponse, status, isDebug);
    }

    private HttpStatus resolveHttpStatus(SimulatedResponse simulatedResponse) {
        HttpStatus status = HttpStatus.resolve(simulatedResponse.statusCode());
        return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }

    private ResponseEntity<Object> buildResponse(SimulatedResponse simulatedResponse, HttpStatus status, boolean isDebug) {
        if (isDebug) {
            return buildDebugResponse(simulatedResponse, status);
        }
        return buildPlainResponse(simulatedResponse, status);
    }

    private ResponseEntity<Object> buildPlainResponse(SimulatedResponse simulatedResponse, HttpStatus status) {
        return ResponseEntity.status(status).body(simulatedResponse.body());
    }

    private ResponseEntity<Object> buildDebugResponse(SimulatedResponse simulatedResponse, HttpStatus status) {
        DebugResponse debugResponse = new DebugResponse(simulatedResponse.meta(), simulatedResponse.body());
        return ResponseEntity.status(status).body(debugResponse);
    }

}

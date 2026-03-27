package com.requestanalysis.requestanalysisservice.simulate.controller;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.model.Simulation;
import com.requestanalysis.requestanalysisservice.simulate.service.FaultSimulationService;
import com.requestanalysis.requestanalysisservice.simulate.service.SimulationHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FaultSimulationController {

    private final FaultSimulationService faultSimulationService;
    private final SimulationHistoryService historyService;

    public FaultSimulationController(FaultSimulationService faultSimulationService, SimulationHistoryService historyService) {
        this.faultSimulationService = faultSimulationService;
        this.historyService = historyService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<Object> simulate(@RequestParam(name = "isDebug", required = false, defaultValue = "false") boolean isDebug) {
        return faultSimulationService.simulateAndBuildResponse(isDebug);
    }

    @GetMapping("/simulate/history")
    public List<Simulation> getHistory() {
        return historyService.getHistory();
    }

    @PostMapping("/configure")
    public void configure(@RequestBody FaultRequestDto request) {
        faultSimulationService.configure(request);
    }

}

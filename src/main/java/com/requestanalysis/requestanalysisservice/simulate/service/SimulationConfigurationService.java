package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SimulationConfigurationService {
    private final AtomicReference<FaultRequestDto> currentConfiguration = new AtomicReference<>();

    public void configure(FaultRequestDto request) {
        currentConfiguration.set(request);
    }

    public FaultRequestDto getCurrentConfiguration() {
        return currentConfiguration.get();
    }
}

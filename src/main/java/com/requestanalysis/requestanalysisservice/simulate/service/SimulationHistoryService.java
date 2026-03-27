package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultResponseMeta;
import com.requestanalysis.requestanalysisservice.simulate.model.Simulation;
import com.requestanalysis.requestanalysisservice.simulate.repository.SimulationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SimulationHistoryService {

    private final SimulationRepository repository;

    public SimulationHistoryService(SimulationRepository repository) {
        this.repository = repository;
    }

    public void save(FaultRequestDto request, String httpMethod, FaultResponseMeta meta, long executionTime) {
        Simulation logEntry = new Simulation(null, httpMethod, request, meta, Instant.now(), executionTime);
        repository.save(logEntry);
    }

    public List<Simulation> getHistory() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}

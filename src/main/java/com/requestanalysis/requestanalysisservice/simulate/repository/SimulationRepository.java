package com.requestanalysis.requestanalysisservice.simulate.repository;

import com.requestanalysis.requestanalysisservice.simulate.model.Simulation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends MongoRepository<Simulation, String> {
}

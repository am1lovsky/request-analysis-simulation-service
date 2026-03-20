package com.requestanalysis.requestanalysisservice.simulate.model;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultResponseMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "simulation_logs")
public class Simulation {
    @Id
    private String id;
    private FaultRequestDto requestDto;
    private FaultResponseMeta responseMeta;
    private Instant createAt;
    private long executionTime;
}

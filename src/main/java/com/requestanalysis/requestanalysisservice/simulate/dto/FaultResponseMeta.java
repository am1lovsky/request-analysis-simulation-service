package com.requestanalysis.requestanalysisservice.simulate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FaultResponseMeta {
    private int statusCode;
    private long delay;
    private boolean isJsonBroken;
    private int responseSize;
    private Instant timestamp; // - for smth like 2025-12-01T15:34:22.123456Z (to store “event time”)
}

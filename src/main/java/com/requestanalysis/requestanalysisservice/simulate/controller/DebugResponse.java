package com.requestanalysis.requestanalysisservice.simulate.controller;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultResponseMeta;

public record DebugResponse(FaultResponseMeta meta, String body) {
}

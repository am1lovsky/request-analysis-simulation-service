package com.requestanalysis.requestanalysisservice.simulate.service;

import com.requestanalysis.requestanalysisservice.simulate.dto.FaultResponseMeta;

public record SimulatedResponse(int statusCode, String body, FaultResponseMeta meta) {
}
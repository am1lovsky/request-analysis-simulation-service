package com.requestanalysis.requestanalysisservice.simulate.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.simulate")
public record FaultSimulationServiceProperties(String patternError,
                                               String patternMessage) {
}

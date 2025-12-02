package com.requestanalysis.requestanalysisservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class RequestAnalysisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestAnalysisServiceApplication.class, args);
	}

}

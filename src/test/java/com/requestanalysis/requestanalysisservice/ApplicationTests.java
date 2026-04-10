package com.requestanalysis.requestanalysisservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.repository.SimulationRepository;
import com.requestanalysis.requestanalysisservice.simulate.model.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SimulationRepository simulationRepository;

    @BeforeEach
    void setUp() throws Exception {
        // Reset configuration to default before each test
        mockMvc.perform(post("/configure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FaultRequestDto())))
                .andExpect(status().isOk());
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testInspectEndpoint() throws Exception {
        mockMvc.perform(get("/inspect/test-path")
                        .header("X-Test-Header", "test-value")
                        .param("param1", "value1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("GET"))
                .andExpect(jsonPath("$.url").value("http://localhost/inspect/test-path"))
                .andExpect(jsonPath("$.headers['X-Test-Header']").value("test-value"))
                .andExpect(jsonPath("$.queryParameters.param1[0]").value("value1"));
    }

    @Test
    void testInspectPostWithBody() throws Exception {
        String testBody = "{\"test\":\"data\"}";
        mockMvc.perform(post("/inspect/post")
                        .content(testBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("POST"))
                .andExpect(jsonPath("$.body").value(testBody));
    }

    @Test
    void testSimulateDefault() throws Exception {
        mockMvc.perform(get("/simulate"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testSimulateConfigureAndCall() throws Exception {
        FaultRequestDto requestDto = new FaultRequestDto();
        requestDto.setStatusCode(418);
        requestDto.setBaseMessage("I am a teapot");
        requestDto.setDelay(100L);

        // Configure
        mockMvc.perform(post("/configure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // Simulate
        mockMvc.perform(put("/simulate"))
                .andExpect(status().is(418))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("I am a teapot")));

        // Verify repository save was called
        verify(simulationRepository).save(any(Simulation.class));
    }

    @Test
    void testSimulateDebugMode() throws Exception {
        FaultRequestDto requestDto = new FaultRequestDto();
        requestDto.setStatusCode(201);
        requestDto.setHttpMethod("PATCH");

        mockMvc.perform(post("/configure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/simulate")
                        .param("isDebug", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.statusCode").value(201))
                .andExpect(jsonPath("$.meta.httpMethod").value("PATCH"))
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    void testSimulateBrokenJson() throws Exception {
        FaultRequestDto requestDto = new FaultRequestDto();
        requestDto.setBrokenJson(true);
        requestDto.setStatusCode(200);

        mockMvc.perform(post("/configure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/simulate"))
                .andExpect(status().isOk())
                // Broken JSON usually means it doesn't end with } or has extra characters
                // Based on SimulationResponseGenerator: patternError + baseMessage + "\" "
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.endsWith("}"))));
    }

    @Test
    void testSimulateCustomBody() throws Exception {
        String customBody = "Custom static body";
        FaultRequestDto requestDto = new FaultRequestDto();
        requestDto.setBody(customBody);
        requestDto.setStatusCode(200);

        mockMvc.perform(post("/configure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/simulate"))
                .andExpect(status().isOk())
                .andExpect(content().string(customBody));
    }

    @Test
    void testSimulateHistoryEndpoint() throws Exception {
        Simulation simulation = new Simulation();
        simulation.setId("1");
        simulation.setHttpMethod("GET");
        simulation.setCreateAt(Instant.now());

        when(simulationRepository.findAll(any(Sort.class))).thenReturn(List.of(simulation));

        mockMvc.perform(get("/simulate/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].httpMethod").value("GET"));
    }

}

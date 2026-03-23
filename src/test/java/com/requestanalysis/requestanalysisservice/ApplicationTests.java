package com.requestanalysis.requestanalysisservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.requestanalysis.requestanalysisservice.simulate.dto.FaultRequestDto;
import com.requestanalysis.requestanalysisservice.simulate.repository.SimulationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SimulationRepository simulationRepository;

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
    void testSimulateEndpoint() throws Exception {
        FaultRequestDto requestDto = new FaultRequestDto();
        requestDto.setStatusCode(200);
        requestDto.setBaseMessage("Success Test");

        mockMvc.perform(post("/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").exists())
                .andExpect(jsonPath("$.body").exists());
    }

    @Test
    void testSimulateHistoryEndpoint() throws Exception {
        when(simulationRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/simulate/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}

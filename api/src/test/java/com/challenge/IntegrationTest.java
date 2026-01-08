package com.challenge.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.api.model.CreateEmployeeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Will test the EmployeeController endpoints. Includes tests for:
 * - GET /api/v1/employee
 * - GET /api/v1/employee/{uuid}
 * - POST /api/v1/employee
 */
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllEmployees_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/employee")).andExpect(status().isOk());
    }

    @Test
    void createEmployee_thenGetByUuid_returns200() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFirstName("Jamie");
        request.setLastName("Smith");
        request.setEmail("jamie.smith@company.com");
        request.setJobTitle("Analyst");
        request.setAge(25);
        request.setSalary(55000);
        request.setContractHireDate(Instant.parse("2026-01-08T15:00:00Z"));

        MvcResult createResult = mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").exists())
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        String uuid = objectMapper.readTree(responseJson).get("uuid").asText();

        mockMvc.perform(get("/api/v1/employee/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid))
                .andExpect(jsonPath("$.firstName").value("Jamie"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void getEmployeeByUuid_whenNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/employee/{uuid}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_whenInvalid_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

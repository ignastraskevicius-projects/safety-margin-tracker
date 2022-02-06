package org.ignast.stockinvesting.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDefineCompany() throws Exception {
        mockMvc.perform(post("/companies/")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        mockMvc.perform(post("/companies/").content("not-a-json-object")).andExpect(status().isBadRequest());
    }

    @Test
    public void companyShouldBeDefinedInJson() throws Exception {
        mockMvc.perform(post("/companies/").content("{}")).andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(post("/companies/").accept("application/json")).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(post("/companies/").accept("application/hal+json")).andExpect(status().isNotAcceptable());
    }

}

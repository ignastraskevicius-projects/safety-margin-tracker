package org.ignast.stockinvesting.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDefineCompany() throws Exception {
        mockMvc.perform(post("/companies/")).andExpect(status().isCreated());
    }
}

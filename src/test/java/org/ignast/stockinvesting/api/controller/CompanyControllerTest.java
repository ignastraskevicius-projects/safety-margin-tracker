package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
public class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)).andExpect(status().isBadRequest())
                .andExpect(content().json("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content("not-a-json-object"))
                .andExpect(status().isBadRequest()).andExpect(content().json("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void companyWithoutAddressShouldBeRejected() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content("{\"name\":\"Santander\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.address\"}"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "null", "\"jsonString\"", "[]" })
    public void companyWithAddressAsNonObjectShouldBeRejected(String addressAsPrimitive) throws Exception {
        String jsonCompany = String.format("{\"name\":\"Amazon\",\"address\":%s}", addressAsPrimitive);
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(jsonCompany))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"$.address\"}"));
    }

    @Test
    public void companyWithoutNameShouldBeRejected() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content("{}")).andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.name\"}"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "null", "{}", "[]" })
    public void companyWithNameAsNonJsonStringShouldBeRejected(String companyName) throws Exception {
        String jsonCompany = String.format("{\"name\":%s,\"address\":{}}", companyName);
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(jsonCompany))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"$.name\"}"));
    }

    @Test
    public void shouldDefineCompany() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content("{\"name\":\"Santander\",\"address\":{}}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(post("/companies/").contentType("application/json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(post("/companies/").contentType("application/hal+json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldIndicateResourceNotReadable() throws Exception {
        mockMvc.perform(get("/companies/").contentType(HAL_JSON)).andExpect(status().isMethodNotAllowed())
                .andExpect(content().string("{\"errorName\":\"methodNotAllowed\"}"));
    }
}

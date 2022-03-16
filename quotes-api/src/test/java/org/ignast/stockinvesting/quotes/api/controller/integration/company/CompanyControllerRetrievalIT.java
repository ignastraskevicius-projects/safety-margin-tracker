package org.ignast.stockinvesting.quotes.api.controller.integration.company;


import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.junit.jupiter.api.Test;

import static org.ignast.stockinvesting.quotes.api.controller.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.*;
import static org.ignast.stockinvesting.testutil.api.traversor.HateoasLink.link;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyControllerRetrievalByIdTest extends CompanyControllerITBase {

    @Test
    public void shouldRetrieveCompany() throws Exception {
        when(companies.findByExternalId(any())).thenReturn(amazon());
        mockMvc.perform(get("/companies/5").accept(V1_QUOTES))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", V1_QUOTES))
                .andExpect(resourceContentMatchesJson(bodyFactory.createAmazon()));
    }

    @Test
    public void retrievedCompanyShouldContainLinkToItself() throws Exception {
        when(companies.findByExternalId(any())).thenReturn(amazon());
        mockMvc.perform(get("/companies/5").accept(V1_QUOTES))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", V1_QUOTES))
                .andExpect(resourceLinksMatchesJson(link("self", "http://localhost/companies/5")));
    }

    @Test
    public void shouldNotFindNonexistentCompany() throws Exception {
        when(companies.findByExternalId(any())).thenThrow(CompanyNotFound.class);
        mockMvc.perform(get("/companies/5").accept(V1_QUOTES))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", V1_QUOTES))
                .andExpect(bodyMatchesJson("{}"));
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(get("/companies/5").accept("application/json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(bodyMatchesJson("{\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(get("/companies/5").accept("application/hal+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(bodyMatchesJson("{\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldIndicateResourceNotModifyable() throws Exception {
        mockMvc.perform(put("/companies/5").contentType(HAL_JSON)).andExpect(status().isMethodNotAllowed())
                .andExpect(bodyMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }

}

package org.ignast.stockinvesting.quotes.api.controller.integration.curie;

import static java.lang.String.format;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.ignast.stockinvesting.quotes.api.controller.CuriesController;
import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ CuriesController.class, AppErrorsHandlingConfiguration.class })
public class CuriesControllerIT {

    private static final String APP_V1 = "application/vnd.stockinvesting.quotes-v1.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetCuriesForCompanyCreation() throws Exception {
        mockMvc
            .perform(get("/rels/quotes/createCompany").accept(APP_V1))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(bodyMatchesJson(format("{'mediaType':'%s','methods':[{'method':'PUT'}]}", APP_V1)));
    }

    @Test
    public void shouldGetCuriesForQuotedPriceRetrieval() throws Exception {
        mockMvc
            .perform(get("/rels/quotes/queryQuotedPrice").accept(APP_V1))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(bodyMatchesJson(format("{'mediaType':'%s','methods':[{'method':'GET'}]}", APP_V1)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "createCompany", "queryQuotedPrice" })
    public void shouldRejectNonHalRequests(final String action) throws Exception {
        mockMvc
            .perform(get("/rels/quotes/" + action).accept("application/json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(bodyMatchesJson("{\"httpStatus\":406,\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "createCompany", "queryQuotedPrice" })
    public void shouldRejectUnversionedRequests(final String action) throws Exception {
        mockMvc
            .perform(get("/rels/quotes/" + action).accept("application/hal+json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(bodyMatchesJson("{\"httpStatus\":406,\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "createCompany", "queryQuotedPrice" })
    public void shouldIndicateResourceNotModifiable(final String action) throws Exception {
        mockMvc
            .perform(put("/rels/quotes/" + action).contentType(APP_V1))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"httpStatus\":405,\"errorName\":\"methodNotAllowed\"}"));
    }
}

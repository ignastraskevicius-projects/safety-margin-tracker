package org.ignast.stockinvesting.quotes.api.controller.root;

import org.ignast.stockinvesting.quotes.api.controller.HalConfig;
import org.ignast.stockinvesting.util.errorhandling.api.GenericErrorHandlingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.ignast.stockinvesting.quotes.util.test.api.HateoasJsonMatchers.hasRel;
import static org.ignast.stockinvesting.quotes.util.test.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ RootController.class, HalConfig.class, GenericErrorHandlingConfiguration.class })
public class RootControllerTest {

    private @Autowired MockMvc mockMvc;

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.quotes-v1.hal+json";

    @Test
    public void rootResourceShouldLinkToCompanies() throws Exception {
        ResultActions root = mockMvc.perform(get("/").accept(V1_MEDIA_TYPE));
        root.andExpect(status().isOk()).andExpect(header().string(CONTENT_TYPE, V1_MEDIA_TYPE))
                .andExpect(content().string(hasRel("quotes:company").withHrefContaining("/companies")));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(get("/").accept("application/hal+json")).andExpect(status().isNotAcceptable())
                .andExpect(bodyMatchesJson("{\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(get("/").accept("application/json")).andExpect(status().isNotAcceptable())
                .andExpect(bodyMatchesJson("{\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldNotBeModifiableResource() throws Exception {
        mockMvc.perform(post("/")).andExpect(status().isMethodNotAllowed())
                .andExpect(bodyMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }
}

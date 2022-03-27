package org.ignast.stockinvesting.quotes.api.controller.root;

import static org.ignast.stockinvesting.testutil.api.HateoasJsonMatchers.hasRel;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.val;
import org.hamcrest.core.StringContains;
import org.ignast.stockinvesting.quotes.api.controller.HalConfig;
import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.BodyValidationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    {
        RootController.class,
        HalConfig.class,
        BodyValidationConfig.class,
        AppErrorsHandlingConfiguration.class,
    }
)
public final class RootControllerTest {

    private static final String V1_MEDIA_TYPE = "application/vnd.stockinvesting.quotes-v1.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void rootResourceShouldLinkToCompanies() throws Exception {
        final val root = mockMvc.perform(get("/").accept(V1_MEDIA_TYPE));
        root
            .andExpect(status().isOk())
            .andExpect(header().string(CONTENT_TYPE, V1_MEDIA_TYPE))
            .andExpect(content().string(hasRel("quotes:createCompany").withHrefContaining("/companies")));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc
            .perform(get("/").accept("application/hal+json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(content().string(new StringContains("mediaTypeNotAcceptable")));
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc
            .perform(get("/").accept("application/json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(content().string(new StringContains("mediaTypeNotAcceptable")));
    }

    @Test
    public void shouldNotBeModifiableResource() throws Exception {
        mockMvc
            .perform(post("/"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"httpStatus\":405,\"errorName\":\"methodNotAllowed\"}"));
    }
}

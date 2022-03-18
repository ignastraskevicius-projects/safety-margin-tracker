package org.ignast.stockinvesting.util.errorhandling.api.integrationtest;

import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.ignast.stockinvesting.util.errorhandling.api.ErrorExtractorConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(ErrorExtractorConfiguration.class)
final class GetMethodRelatedErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc
            .perform(get("/").accept("application/hal+json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(bodyMatchesJson("{\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldRejectNonAppSpecificHalRequests() throws Exception {
        mockMvc
            .perform(get("/").accept("application/json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(bodyMatchesJson("{\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldNotBeModifiableResource() throws Exception {
        mockMvc
            .perform(post("/"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + "/";
        }

        @GetMapping(value = "/", produces = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest() {
            return new HttpEntity<>("");
        }
    }
}

@WebMvcTest(ErrorExtractorConfiguration.class)
final class WriteMethodRelatedErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc
            .perform(post("/").contentType("application/json"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(bodyMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectNonAppSpecificHalRequests() throws Exception {
        mockMvc
            .perform(post("/").contentType("application/hal+json"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(bodyMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldIndicateResourceNotReadable() throws Exception {
        mockMvc
            .perform(get("/").contentType(HAL_JSON))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + "/";
        }

        @PostMapping(value = "/", consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest() {
            return new HttpEntity<>("");
        }
    }
}

package org.ignast.stockinvesting.util.test.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class NonExtensibleContentMatchers {
    public static ResultMatcher contentMatchesJson(String expectedJson) {
        return (result) -> {
            String actualJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        };
    }
}

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
final class NonExtensibleContentMatchersTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext appContext;

    private final NonExtensibleContentMatchers matcher = new NonExtensibleContentMatchers();

    @BeforeEach
    private void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
    }

    @Test
    public void shouldVerifySameJson() throws Exception {
        mockMvc.perform(get("/orderedNumbers")).andExpect(status().isOk())
                .andExpect(matcher.contentMatchesJson("{\"list\":[1,2,3]}"));
    }

    @Test
    public void shouldVerifyJsonWithExtraFields() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> mockMvc.perform(get("/propertiesAandB"))
                .andExpect(status().isOk()).andExpect(matcher.contentMatchesJson("{\"A\":\"a\"}")));
    }

    @Test
    public void shouldVerifyJsonWithUnorderedFields() throws Exception {
        mockMvc.perform(get("/orderedNumbers")).andExpect(status().isOk())
                .andExpect(matcher.contentMatchesJson("{\"list\":[1,3,2]}"));
    }

    @Configuration
    public static class TestConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    public static class TestController {

        @RequestMapping("orderedNumbers")
        public String list() {
            return "{\"list\":[1,2,3]}";
        }

        @RequestMapping("propertiesAandB")
        public String twoProperties() {
            return "{\"A\":\"a\",\"B\":\"b\"}";
        }
    }
}

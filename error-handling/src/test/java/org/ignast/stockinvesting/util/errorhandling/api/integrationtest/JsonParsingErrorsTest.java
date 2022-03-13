package org.ignast.stockinvesting.util.errorhandling.api.integrationtest;

import lombok.Getter;
import org.ignast.stockinvesting.util.errorhandling.api.GenericErrorHandlingConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.ignast.stockinvesting.util.errorhandling.util.test.api.NonExtensibleContentMatchers.contentMatchesJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenericErrorHandlingConfiguration.class)
class UnparsableJsonErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("not-a-json-object"))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
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

        static String rootResourceOn(int port) {
            return "http://localhost:" + port + "/";
        }

        @PostMapping(value = "/", consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@RequestBody TestDTO dto) {
            return new HttpEntity<>("");
        }
    }

    class TestDTO {
    }
}

@WebMvcTest(GenericErrorHandlingConfiguration.class)
class JsonStringFieldErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.testField\"}]}"));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testField\":null}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.testField\"}]}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "3.3", "true", "false", "{}", "[]"})
    public void shouldRejectRequestWithWrongTypeWhereStringIsRequired(String wrongType) throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content(format("{\"testField\":%s}", wrongType))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"$.testField\"}]}"));
    }

    @Test
    public void shouldRejectRequestsNotComplyingWithBusinessRequirements() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testField\":\"someValue\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"$.testField\",\"message\":\"failed with test domain requirement\"}]}"));
    }

    @TestConfiguration
    static class TestControllerConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public DomainClassConstraint.SupportedTypes supportedTypes() {
            return DomainClassConstraint.SupportedTypes.supporting(Map.of(TestDomain.class, TestDomain::new), emptyMap());
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(int port) {
            return "http://localhost:" + port + "/";
        }

        @PostMapping(value = "/", consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Valid @RequestBody TestDTO dto) {
            return new HttpEntity<>("");
        }
    }

    @Getter
    static class TestDTO {
        @NotNull
        @DomainClassConstraint(domainClass = TestDomain.class)
        private String testField;
    }

    static class TestDomain {
        TestDomain(String arg) {
            throw new IllegalArgumentException("failed with test domain requirement");
        }
    }
}

@WebMvcTest(GenericErrorHandlingConfiguration.class)
class JsonIntegerFieldErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.integerField\"}]}"));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"integerField\":null}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.integerField\"}]}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3.3", "\"nonInteger\"", "true", "false", "{}", "[]"})
    public void shouldRejectRequestIndicatingWrongTypeWhereIntegerIsRequired(String wrongType) throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content(format("{\"integerField\":%s}", wrongType))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeInteger\",\"jsonPath\":\"$.integerField\"}]}"));
    }

    @Test
    public void shouldRejectRequestsNotComplyingWithBusinessRequirements() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"integerField\":5}"))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"$.integerField\",\"message\":\"failed with test domain requirement\"}]}"));
    }

    @TestConfiguration
    static class TestControllerConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public DomainClassConstraint.SupportedTypes supportedTypes() {
            return DomainClassConstraint.SupportedTypes.supporting(emptyMap(), Map.of(TestDomain.class, TestDomain::new));
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(int port) {
            return "http://localhost:" + port + "/";
        }

        @PostMapping(value = "/", consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Valid @RequestBody TestDTO dto) {
            return new HttpEntity<>("");
        }
    }

    @Getter
    static class TestDTO {
        @NotNull
        @DomainClassConstraint(domainClass = TestDomain.class)
        private Integer integerField;
    }

    static class TestDomain {
        TestDomain(Integer arg) {
            throw new IllegalArgumentException("failed with test domain requirement");
        }
    }
}

@WebMvcTest(GenericErrorHandlingConfiguration.class)
class JsonCollectionErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.testList\"}]}"));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testList\":null}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.testList\"}]}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "3.3", "true", "false", "{}", "\"string\""})
    public void shouldRejectRequestWithWrongTypeWhereStringIsRequired(String wrongType) throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content(format("{\"testList\":%s}", wrongType))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"$.testList\"}]}"));
    }

    @Test
    public void shouldRejectRequestContainingLessElementsThanRequired() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testList\":[]}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"$.testList\",\"message\":\"message for too few\"}]}"));
    }

    @Test
    public void shouldRejectRequestsContainingMoreElementsThanRequired() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testList\":[{},{}]}"))
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"$.testList\",\"message\":\"message for too many\"}]}"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false", "3", "3.3", "\"someString\", []" })
    public void shouldRejectRequestsContainingElementsOfWrongTypeWhereObjectIsRequired(String listingOfWrongType)
            throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content(format("{\"testList\":[%s]}", listingOfWrongType)))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeObject\",\"jsonPath\":\"$.testList[0]\"}]}"));
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

        static String rootResourceOn(int port) {
            return "http://localhost:" + port + "/";
        }

        @PostMapping(value = "/", consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Valid @RequestBody TestDTO dto) {
            return new HttpEntity<>("");
        }
    }

    @Getter
    static class TestDTO {
        @NotNull
        @Size(min = 1, message = "message for too few")
        @Size(max = 1, message = "message for too many")
        private List<TestElementDTO> testList;
    }

    static class TestElementDTO {}
}

@WebMvcTest(GenericErrorHandlingConfiguration.class)
class JsonNestedStringFieldErrorsTest {

    @Autowired
    private MockMvc mockMvc;

    static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testList\":[{}]}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.testList[0].testField\"}]}"));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content("{\"testList\":[{\"testField\":null}]}")).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"$.testList[0].testField\"}]}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "3.3", "true", "false", "{}", "[]"})
    public void shouldRejectRequestWithNestedWrongTypeWhereStringIsRequired(String wrongType) throws Exception {
        mockMvc.perform(post("/").contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                        .content(format("{\"testList\":[{\"testField\":%s}]}}", wrongType))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"$.testList[0].testField\"}]}"));
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

        static String rootResourceOn(int port) {
            return "http://localhost:" + port + "/";
        }

        @PostMapping(value = "/", consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Validated @RequestBody TestDTO dto) {
            return new HttpEntity<>("");
        }
    }

    @Getter
    static class TestDTO {
        @NotNull
        @Valid
        private List<TestElementDTO> testList;
    }

    @Getter
    static class TestElementDTO {
        @NotNull
        private String testField;
    }
}

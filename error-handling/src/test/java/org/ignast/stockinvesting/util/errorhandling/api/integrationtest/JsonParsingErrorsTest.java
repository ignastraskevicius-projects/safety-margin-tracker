package org.ignast.stockinvesting.util.errorhandling.api.integrationtest;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forArrayRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forIntegerRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forInvalidValueAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forObjectRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forStringRequiredAt;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.ErrorExtractorConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("checkstyle:outertypefilename")
@WebMvcTest(ErrorExtractorConfiguration.class)
final class UnparsableJsonErrorsTest {

    private static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    private static final String ROOT = "/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson("{\"httpStatus\":400,\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("not-a-json-object"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson("{\"httpStatus\":400,\"errorName\":\"bodyNotParsable\"}"));
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public MediaType appMediaType() {
            return MediaType.APPLICATION_PDF;
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + ROOT;
        }

        @PostMapping(value = ROOT, consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@RequestBody final TestDTO b) {
            return new HttpEntity<>("");
        }
    }

    class TestDTO {}
}

@WebMvcTest(ErrorExtractorConfiguration.class)
final class JsonStringFieldErrorsTest {

    private static final String ROOT = "/";

    private static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.testField")));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"testField\":null}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.testField")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldRejectRequestWithWrongTypeWhereStringIsRequired(final String wrongType)
        throws Exception {
        mockMvc
            .perform(
                post(ROOT)
                    .contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                    .content(format("{\"testField\":%s}", wrongType))
            )
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forStringRequiredAt("$.testField")));
    }

    @Test
    public void shouldRejectRequestsNotComplyingWithBusinessRequirements() throws Exception {
        mockMvc
            .perform(
                post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"testField\":\"someValue\"}")
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                bodyMatchesJson(forInvalidValueAt("$.testField", "failed with test domain requirement"))
            );
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public MediaType appMediaType() {
            return MediaType.APPLICATION_PDF;
        }

        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public DomainClassConstraint.SupportedTypes supportedTypes() {
            return DomainClassConstraint.SupportedTypes.supporting(
                Map.of(TestDomain.class, TestDomain::new),
                emptyMap()
            );
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + ROOT;
        }

        @PostMapping(value = ROOT, consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Valid @RequestBody final TestDTO dto) {
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

        TestDomain(final String arg) {
            throw new IllegalArgumentException("failed with test domain requirement");
        }
    }
}

@WebMvcTest(ErrorExtractorConfiguration.class)
final class JsonIntegerFieldErrorsTest {

    private static final String ROOT = "/";

    private static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.integerField")));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"integerField\":null}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.integerField")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3.3", "\"nonInteger\"", "true", "false", "{}", "[]" })
    public void shouldRejectRequestIndicatingWrongTypeWhereIntegerIsRequired(final String wrongType)
        throws Exception {
        mockMvc
            .perform(
                post(ROOT)
                    .contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                    .content(format("{\"integerField\":%s}", wrongType))
            )
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forIntegerRequiredAt("$.integerField")));
    }

    @Test
    public void shouldRejectRequestsNotComplyingWithBusinessRequirements() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"integerField\":5}"))
            .andExpect(status().isBadRequest())
            .andExpect(
                bodyMatchesJson(forInvalidValueAt("$.integerField", "failed with test domain requirement"))
            );
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public MediaType appMediaType() {
            return MediaType.APPLICATION_PDF;
        }

        @Bean
        public DomainClassConstraint.SupportedTypes supportedTypes() {
            return DomainClassConstraint.SupportedTypes.supporting(
                emptyMap(),
                Map.of(TestDomain.class, TestDomain::new)
            );
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + ROOT;
        }

        @PostMapping(value = ROOT, consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Valid @RequestBody final TestDTO b) {
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

        TestDomain(final Integer arg) {
            throw new IllegalArgumentException("failed with test domain requirement");
        }
    }
}

@WebMvcTest(ErrorExtractorConfiguration.class)
final class JsonCollectionErrorsTest {

    private static final String ROOT = "/";

    private static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.testList")));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"testList\":null}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.testList")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "\"string\"" })
    public void shouldRejectRequestWithWrongTypeWhereStringIsRequired(final String wrongType)
        throws Exception {
        mockMvc
            .perform(
                post(ROOT)
                    .contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                    .content(format("{\"testList\":%s}", wrongType))
            )
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forArrayRequiredAt("$.testList")));
    }

    @Test
    public void shouldRejectRequestContainingLessElementsThanRequired() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"testList\":[]}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forInvalidValueAt("$.testList", "message for too few")));
    }

    @Test
    public void shouldRejectRequestsContainingMoreElementsThanRequired() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"testList\":[{},{}]}"))
            .andExpect(bodyMatchesJson(forInvalidValueAt("$.testList", "message for too many")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false", "3", "3.3", "\"someString\", []" })
    public void shouldRejectRequestsContainingElementsOfWrongTypeWhereObjectIsRequired(
        final String listingOfWrongType
    ) throws Exception {
        final val body = format("{\"testList\":[%s]}", listingOfWrongType);
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content(body))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forObjectRequiredAt("$.testList[0]")));
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public MediaType appMediaType() {
            return MediaType.APPLICATION_PDF;
        }

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + ROOT;
        }

        @PostMapping(value = ROOT, consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Valid @RequestBody final TestDTO b) {
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

@WebMvcTest(ErrorExtractorConfiguration.class)
final class JsonNestedStringFieldErrorsTest {

    private static final String ROOT = "/";

    private static final String RESOURCE_SPECIFIC_MEDIA_TYPE = "application/resourceSpecificHeader.hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectRequestWithoutMandatoryField() throws Exception {
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content("{\"testList\":[{}]}"))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.testList[0].testField")));
    }

    @Test
    public void shouldRejectRequestWhereNullIsSubmittedForMandatoryField() throws Exception {
        mockMvc
            .perform(
                post(ROOT)
                    .contentType(RESOURCE_SPECIFIC_MEDIA_TYPE)
                    .content("{\"testList\":[{\"testField\":null}]}")
            )
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forMissingFieldAt("$.testList[0].testField")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldRejectRequestWithNestedWrongTypeWhereStringIsRequired(final String wrongType)
        throws Exception {
        final val body = format("{\"testList\":[{\"testField\":%s}]}}", wrongType);
        mockMvc
            .perform(post(ROOT).contentType(RESOURCE_SPECIFIC_MEDIA_TYPE).content(body))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson(forStringRequiredAt("$.testList[0].testField")));
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public MediaType appMediaType() {
            return MediaType.APPLICATION_PDF;
        }

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        static String rootResourceOn(final int port) {
            return "http://localhost:" + port + ROOT;
        }

        @PostMapping(value = ROOT, consumes = RESOURCE_SPECIFIC_MEDIA_TYPE)
        public HttpEntity<String> updateTest(@Validated @RequestBody final TestDTO b) {
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

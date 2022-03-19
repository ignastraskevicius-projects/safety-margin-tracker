package org.ignast.stockinvesting.api.controller;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forArrayRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forInvalidValueAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forObjectRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forStringRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forTwoMissingFieldsAt;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.ignast.stockinvesting.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.domain.Companies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
abstract class CompanyControllerIntegrationTestBase {

    protected CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    @Autowired
    protected MockMvc mockMvc;

    protected final String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    MockMvcAssert assertThatRequest(final String body) throws Exception {
        return new MockMvcAssert(
            mockMvc.perform(put("/companies/").contentType(V1_MEDIA_TYPE).content(body))
        );
    }

    @RequiredArgsConstructor
    static final class MockMvcAssert {

        private final ResultActions mockMvcResult;

        void failsValidation(final String expectedResponse) throws Exception {
            mockMvcResult.andExpect(status().isBadRequest()).andExpect(bodyMatchesJson(expectedResponse));
        }
    }
}

public final class CompanyControllerIntegrationTest extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType(V1_MEDIA_TYPE))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        assertThatRequest("not-a-json-object").failsValidation("{\"errorName\":\"bodyNotParsable\"}");
    }

    @Test
    public void shouldDefineCompany() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createAmazon()))
            .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType("application/json"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(bodyMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType("application/hal+json"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(bodyMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldIndicateResourceNotReadable() throws Exception {
        mockMvc
            .perform(get("/companies/").contentType(HAL_JSON))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }

    @Test
    public void shouldAbleToPreserveErrorsFromMultipleFields() throws Exception {
        assertThatRequest(bodyFactory.createWithoutNameAndCurrency())
            .failsValidation(forTwoMissingFieldsAt("$.name", "$.functionalCurrency"));
    }
}

final class CompanyControllerIdParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void shouldRejectCompanyWithoutIdIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("")).failsValidation(forMissingFieldAt("$.id"));
    }

    @Test
    public void shouldRejectCompanyWithNonStringIdIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("\"id\":3"))
            .failsValidation(forStringRequiredAt("$.id"));
    }

    @Test
    public void shouldRejectCompaniesWithUnparsableIdAsUUID() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("\"id\":\"19c56404-73c6-4cd1-96a4-aae7962b643z\""))
            .failsValidation(
                forInvalidValueAt(
                    "$.id",
                    "Must consist of hyphens (-) and a,b,c,d,e,f and numeric characters only"
                )
            );
    }
}

final class CompanyControllerCurrencyParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void shouldRejectCompanyWithoutCurrencyIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithFunctionalCurrencyJsonPair(""))
            .failsValidation(forMissingFieldAt("$.functionalCurrency"));
    }

    @Test
    public void shouldRejectCompanyWithCurrencyAsNonStringIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":3"))
            .failsValidation(forStringRequiredAt("$.functionalCurrency"));
    }

    @Test
    public void shouldRejectInvalidCurrencyCode() throws Exception {
        assertThatRequest(bodyFactory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"US\""))
            .failsValidation(forInvalidValueAt("$.functionalCurrency", "Currency must have 3 letters"));
    }
}

final class CompanyControllerHomeCountryParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void shouldRejectCompanyWithoutCountryIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithHomeCountryJsonPair(""))
            .failsValidation(forMissingFieldAt("$.homeCountry"));
    }

    @Test
    public void shouldRejectCompanyWithNullCountryIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":null"))
            .failsValidation(forMissingFieldAt("$.homeCountry"));
    }

    @Test
    public void shouldRejectCompanyWithNonStringAsNonJsonStringIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":3"))
            .failsValidation(forStringRequiredAt("$.homeCountry"));
    }

    @Test
    public void shouldRejectTooShortCountryCode() throws Exception {
        assertThatRequest(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":\"S\""))
            .failsValidation(forInvalidValueAt("$.homeCountry", "Must consist of 2 characters"));
    }

    @Test
    public void shouldRejectTooLongCountryCode() throws Exception {
        assertThatRequest(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":\"USA\""))
            .failsValidation(forInvalidValueAt("$.homeCountry", "Must consist of 2 characters"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "12", "us", "ÑÑ" })
    public void shouldRejectCountryCodesContainingNonUppercaseCharacters(final String countryCode)
        throws Exception {
        assertThatRequest(
            bodyFactory.createWithHomeCountryJsonPair(format("\"homeCountry\":\"%s\"", countryCode))
        )
            .failsValidation(
                forInvalidValueAt("$.homeCountry", "Must contain only uppercase latin characters")
            );
    }

    @Test
    public void shouldRejectInvalidISO3166alpha2CountryCode() throws Exception {
        assertThatRequest(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":\"AB\""))
            .failsValidation(forInvalidValueAt("$.homeCountry", "Must be a valid ISO 3166 alpha-2 code"));
    }
}

final class CompanyControllerNameParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    private static final int MAX_COMPANY_NAME_LENGTH = 160;

    @MockBean
    private Companies companies;

    @Test
    public void shouldRejectCompanyWithoutNameIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithNameJsonPair(""))
            .failsValidation(forMissingFieldAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithNameAsNonJsonStringIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithNameJsonPair("\"name\":false"))
            .failsValidation(forStringRequiredAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithEmptyName() throws Exception {
        assertThatRequest(companyWithNameOfLength(0))
            .failsValidation(forInvalidValueAt("$.name", "Company name must be between 1-160 characters"));
    }

    @Test
    public void shouldCreateCompanyWithAtLeast1Character() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(1)))
            .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectCompanyWithTooLongName() throws Exception {
        final val overMaxLength = MAX_COMPANY_NAME_LENGTH + 1;
        assertThatRequest(companyWithNameOfLength(overMaxLength))
            .failsValidation(forInvalidValueAt("$.name", "Company name must be between 1-160 characters"));
    }

    @Test
    public void shouldCreateCompanyWithWithNamesOfRelativelyReasonableLength() throws Exception {
        mockMvc
            .perform(
                put("/companies/")
                    .contentType(V1_MEDIA_TYPE)
                    .content(companyWithNameOfLength(MAX_COMPANY_NAME_LENGTH))
            )
            .andExpect(status().isCreated());
    }

    private String companyWithNameOfLength(final int length) {
        final val name = "c".repeat(length);
        return bodyFactory.createWithNameJsonPair(format("\"name\":\"%s\"", name));
    }
}

final class CompanyControllerListingsParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void companyWithoutListingShouldBeRejectedIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair(""))
            .failsValidation(forMissingFieldAt("$.listings"));
    }

    @Test
    public void companyWithNonArrayListingShouldBeRejectedIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":3"))
            .failsValidation(forArrayRequiredAt("$.listings"));
    }

    @Test
    public void companyWithZeroListingsShouldBeRejected() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[]"))
            .failsValidation(
                forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange")
            );
    }

    @Test
    public void companyWithNullListingShouldBeRejected() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[null]"))
            .failsValidation(
                forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange")
            );
    }

    @Test
    public void companyWithIndividualListingAsNonObjectShouldBeRejectedIndicatedWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[3.3]"))
            .failsValidation(forObjectRequiredAt("$.listings[0]"));
    }

    @Test
    public void shouldNotSupportMultipleListings() throws Exception {
        assertThatRequest(bodyFactory.createWithMultipleListings())
            .failsValidation(forInvalidValueAt("$.listings", "Multiple listings are not supported"));
    }
}

final class CompanyControllerTestIndividualListingParsingIntegrationTest
    extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void shouldRejectCompanyListedWithoutMarketIdIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithMarketIdJsonPair(""))
            .failsValidation(forMissingFieldAt("$.listings[0].marketIdentifier"));
    }

    @Test
    public void shouldRejectCompanyListedWithNonStringMarketIdIndicatingTypeIsWrong() throws Exception {
        assertThatRequest(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":true"))
            .failsValidation(forStringRequiredAt("$.listings[0].marketIdentifier"));
    }

    @Test
    public void shouldRejectCompanyListedWithInvalidMarketId() throws Exception {
        assertThatRequest(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":\"invalid\""))
            .failsValidation(
                forInvalidValueAt(
                    "$.listings[0].marketIdentifier",
                    "Market Identifier is not 4 characters long (ISO 10383 standard)"
                )
            );
    }

    @Test
    public void shouldRejectCompanyWithoutSymbolIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithSymbolJsonPair(""))
            .failsValidation(forMissingFieldAt("$.listings[0].stockSymbol"));
    }

    @Test
    public void shouldRejectCompanyWithNonStringSymbolIndicatingTypeIsWrong() throws Exception {
        assertThatRequest(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":3"))
            .failsValidation(forStringRequiredAt("$.listings[0].stockSymbol"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidSymbol() throws Exception {
        assertThatRequest(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":\"TOOLONG\""))
            .failsValidation(
                forInvalidValueAt(
                    "$.listings[0].stockSymbol",
                    "Stock Symbol must contain between 1-5 characters"
                )
            );
    }
}

final class CompanyControllerIntegrationTestBaseTest extends CompanyControllerIntegrationTestBase {

    @MockBean
    private Companies companies;

    @Test
    public void shouldNotRejectGoodRequest() {
        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> assertThatRequest(bodyFactory.createAmazon()).failsValidation("any"))
            .withMessage("Status expected:<400> but was:<201>");
    }

    @Test
    public void shouldRejectBadRequest() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("")).failsValidation(forMissingFieldAt("$.id"));
    }
}

package org.ignast.stockinvesting.api.controller;

import org.ignast.stockinvesting.api.controller.errorhandler.GenericErrorHandlingConfiguration;
import org.ignast.stockinvesting.api.controller.errorhandler.annotations.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.domain.Companies;
import org.ignast.stockinvesting.estimates.domain.StockQuotes;
import org.ignast.stockinvesting.estimates.domain.StockSymbolNotSupported;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.ignast.stockinvesting.api.controller.BodySchemaMismatchJsonErrors.*;
import static org.ignast.stockinvesting.api.controller.NonExtensibleContentMatchers.contentMatchesJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
@Import(AppErrorsHandlingConfiguration.class)
public class CompanyControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockQuotes quotes;

    @MockBean
    private Companies companies;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content("not-a-json-object"))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldDefineCompany() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createAmazon()))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(post("/companies/").contentType("application/json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(contentMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(post("/companies/").contentType("application/hal+json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(contentMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldIndicateResourceNotReadable() throws Exception {
        mockMvc.perform(get("/companies/").contentType(HAL_JSON)).andExpect(status().isMethodNotAllowed())
                .andExpect(contentMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }

    @Test
    public void shouldAbleToPreserveErrorsFromMultipleFields() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithoutNameAndCurrency()))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forTwoMissingFieldsAt("$.name", "$.functionalCurrency")));
    }
}

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
class CompanyControllerCurrencyParsingIntegrationTest {

    @MockBean
    private Companies companies;

    @MockBean
    private StockQuotes quotes;

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompanyWithoutCurrencyIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair(""))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.functionalCurrency")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldRejectCompanyWithCurrencyAsNonStringIndicatingWrongType(String currency) throws Exception {
        String currencyJsonPair = "\"functionalCurrency\":" + currency;
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair(currencyJsonPair)))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.functionalCurrency")));
    }

    @Test
    public void shouldRejectTooShortCurrencyCode() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"US\"")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValuesAt("$.functionalCurrency", "Currency must have 3 letters",
                        "$.functionalCurrency", "Currency must be a valid ISO 4217 code")));
    }

    @Test
    public void shouldRejectTooLongCurrencyCode() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"USDOLLAR\"")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValuesAt("$.functionalCurrency", "Currency must have 3 letters",
                        "$.functionalCurrency", "Currency must be a valid ISO 4217 code")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "usd", "ÑÑÑ" })
    public void shouldRejectCurrencyCodesContainingNonUppercaseCharacters(String currencyCode) throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair(
                        String.format("\"functionalCurrency\":\"%s\"", currencyCode))))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValuesAt("$.functionalCurrency",
                        "Currency must contain only uppercase latin characters", "$.functionalCurrency",
                        "Currency must be a valid ISO 4217 code")));
    }

    @Test
    public void shouldRejectInvalidISO4217Currency() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"ABC\"")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.functionalCurrency", "Currency must be a valid ISO 4217 code")));
    }
}

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
class CompanyControllerHomeCountryParsingIntegrationTest {

    @MockBean
    private Companies companies;

    @MockBean
    private StockQuotes quotes;

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompanyWithoutCountryIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithHomeCountryJsonPair("")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.homeCountry")));
    }

    @Test
    public void shouldRejectCompanyWithNullCountryIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":null")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.homeCountry")));
    }

    @Test
    public void shouldRejectCompanyWithNonStringAsNonJsonStringIndicatingWrongType() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":3")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forStringRequiredAt("$.homeCountry")));
    }

    @Test
    public void shouldRejectTooShortCountryCode() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":\"S\"")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forInvalidValuesAt("$.homeCountry",
                        "Must consist of 2 characters", "$.homeCountry", "Must be a valid ISO 3166 alpha-2 code")));
    }

    @Test
    public void shouldRejectTooLongCountryCode() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":\"USA\"")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forInvalidValuesAt("$.homeCountry",
                        "Must consist of 2 characters", "$.homeCountry", "Must be a valid ISO 3166 alpha-2 code")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "12", "us", "ÑÑ" })
    public void shouldRejectCountryCodesContainingNonUppercaseCharacters(String countryCode) throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(
                bodyFactory.createWithHomeCountryJsonPair(String.format("\"homeCountry\":\"%s\"", countryCode))))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(
                        forInvalidValuesAt("$.homeCountry", "Must contain only uppercase latin characters",
                                "$.homeCountry", "Must be a valid ISO 3166 alpha-2 code")));
    }

    @Test
    public void shouldRejectInvalidISO3166alpha2CountryCode() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithHomeCountryJsonPair("\"homeCountry\":\"AB\"")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.homeCountry", "Must be a valid ISO 3166 alpha-2 code")));
    }

}

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
class CompanyControllerNameParsingIntegrationTest {

    @MockBean
    private Companies companies;

    @MockBean
    private StockQuotes quotes;

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompanyWithoutNameIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithNameJsonPair("")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.name")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldRejectCompanyWithNameAsNonJsonStringIndicatingWrongType(String nameValue) throws Exception {
        String nameJsonPair = "\"name\":" + nameValue;
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithNameJsonPair(nameJsonPair))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.name")));
    }

    @Test
    public void shouldRejectCompanyWithNullNameIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithNameJsonPair("\"name\":null"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.name")));
    }

    @Test
    public void shouldRejectCompanyWithEmptyName() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(0)))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.name", "Company name must be between 1-255 characters")));
    }

    @Test
    public void shouldCreateCompanyWithAtLeast1Character() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(1)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectCompanyWithTooLongName() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(256)))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.name", "Company name must be between 1-255 characters")));
    }

    @Test
    public void shouldCreateCompanyWithWithNamesOfRelativelyReasonableLength() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(255)))
                .andExpect(status().isCreated());
    }

    private String companyWithNameOfLength(int length) {
        String name = "c".repeat(length);
        return bodyFactory.createWithNameJsonPair(String.format("\"name\":\"%s\"", name));
    }
}

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
class CompanyControllerListingsParsingIntegrationTest {

    @MockBean
    private Companies companies;

    @MockBean
    private StockQuotes quotes;

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void companyWithoutListingShouldBeRejectedIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithListingsJsonPair("")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.listings")));
    }

    @Test
    public void companyWithNullListingShouldBeRejectedIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithListingsJsonPair("\"listings\":null")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.listings")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "\"jsonString\"" })
    public void companyWithNonArrayListingShouldBeRejectedIndicatingWrongType() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithListingsJsonPair("\"listings\":3"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forArrayRequiredAt("$.listings")));
    }

    @Test
    public void companyWithZeroListingsShouldBeRejected() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithListingsJsonPair("\"listings\":[]"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(
                        forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange")));
    }

    @Test
    public void companyWithNullListingShouldBeRejected() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithListingsJsonPair("\"listings\":[null]")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false", "3", "3.3", "\"someString\", []" })
    public void companyWithIndividualListingAsNonObjectShouldBeRejectedIndicatedWrongType(String listingOfWrongType)
            throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(
                bodyFactory.createWithListingsJsonPair(String.format("\"listings\":[%s]", listingOfWrongType))))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forObjectRequiredAt("$.listings[0]")));
    }

    @Test
    public void shouldNotSupportMultipleListings() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithMultipleListings()))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValueAt("$.listings", "Multiple listings are not supported")));
    }
}

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
class CompanyControllerTestIndividualListingParsingIntegrationTest {

    @MockBean
    private Companies companies;

    @MockBean
    private StockQuotes quotes;

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompanyListedWithoutMarketIdIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithMarketIdJsonPair("")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.listings[0].marketIdentifier")));
    }

    @Test
    public void shouldRejectCompanyListedWithNullMarketIdIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":null")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.listings[0].marketIdentifier")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldRejectCompanyListedWithNonStringMarketIdIndicatingTypeIsWrong(String marketId) throws Exception {
        String marketIdentifier = "\"marketIdentifier\":" + marketId;
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithMarketIdJsonPair(marketIdentifier))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.listings[0].marketIdentifier")));
    }

    @Test
    public void shouldRejectCompanyListedWithNon4characterMarketId() throws Exception {
        String marketIdentifier = "\"marketIdentifier\":\"invalid\"";
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithMarketIdJsonPair(marketIdentifier))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValueAt("$.listings[0].marketIdentifier",
                        "Market Identifier is not 4 characters long (ISO 10383 standard)")));
    }

    @Test
    public void shouldRejectCompanyListedWithNonUppercaseCharacterMarketId() throws Exception {
        String marketIdentifier = "\"marketIdentifier\":\"xnys\"";
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithMarketIdJsonPair(marketIdentifier))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValueAt("$.listings[0].marketIdentifier",
                        "Market Identifier must contain only latin uppercase alphanumeric characters (ISO 10383 standard)")));
    }

    @Test
    public void shouldRejectCompanyWithoutSymbolIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithSymbolJsonPair("")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.listings[0].stockSymbol")));
    }

    @Test
    public void shouldRejectCompanyWithNonStringSymbolIndicatingTypeIsWrong() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":3"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.listings[0].stockSymbol")));
    }

    @Test
    public void shouldRejectCompanyWithInvalidSymbol() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":\"TOOLONG\"")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValueAt("$.listings[0].stockSymbol",
                        "Stock Symbol must contain between 1-5 characters")));
    }

    @Test
    public void shouldRejectCompanyWithUnsupportedSymbol() throws Exception {
        doThrow(StockSymbolNotSupported.class).when(companies).create(any());
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":\"BBBB\"")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"stockSymbolNotSupported\"}"));
    }
}
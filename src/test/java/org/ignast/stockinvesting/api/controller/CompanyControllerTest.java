package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.ignast.stockinvesting.api.controller.BodySchemaMismatchJsonErrors.*;
import static org.ignast.stockinvesting.api.controller.NonExtensibleContentMatchers.contentMatchesJson;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
public class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
class CompanyControllerCurrencyParsingTest {

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
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.functionalCurrency", "Currency must have 3 letters (ISO 4217)")));
    }

    @Test
    public void shouldRejectTooLongCurrencyCode() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair("\"functionalCurrency\":\"USDollar\"")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(
                        forInvalidValueAt("$.functionalCurrency", "Currency must have 3 letters (ISO 4217)")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "usd", "ÑÑÑ" })
    public void shouldRejectCurrencyCodesContainingNonUppercaseCharacters(String currencyCode) throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithFunctionalCurrencyJsonPair(
                        String.format("\"functionalCurrency\":\"%s\"", currencyCode))))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValueAt("$.functionalCurrency",
                        "Currency must contain only uppercase latin characters")));
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
class CompanyControllerAddressParsingTest {

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompanyWithoutAddressIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithAddressJsonPair("")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.address")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "\"jsonString\"", "[]" })
    public void shouldRejectCompanyWithAddressAsNonObjectIndicatingWrongType(String addressValue) throws Exception {
        String addressJsonPair = "\"address\":" + addressValue;
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithAddressJsonPair(addressJsonPair))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forObjectRequiredAt("$.address")));
    }

    @Test
    public void shouldRejectCompanyWithNullAddressIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithAddressJsonPair("\"address\":null"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.address")));
    }

    @Test
    public void shouldRejectCompanyWithoutCountryIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithCountryJsonPair("")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.address.country")));
    }

    @Test
    public void shouldRejectCompanyWithNullCountryIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithCountryJsonPair("\"country\":null"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.address.country")));
    }

    @Test
    public void shouldRejectCompanyWithNonStringAsNonJsonStringIndicatingWrongType() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithCountryJsonPair("\"country\":3"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.address.country")));
    }

}

@WebMvcTest
class CompanyControllerNameParsingTest {

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
class CompanyControllerListingsParsingTest {

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
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithListingsJsonPair(
                "\"listings\":[{\"stockExchange\":\"New York Stock Exchange\",\"ticker\":\"Amazon\"}, {\"stockExchange\":\"London Stock Exchange\",\"ticker\":\"Amazon\"}]")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forInvalidValueAt("$.listings", "Multiple listings are not supported")));
    }
}

@WebMvcTest
class CompanyControllerTestIndividualListingParsingTest {

    @Autowired
    private MockMvc mockMvc;

    private CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void shouldRejectCompanyListedWithoutStockExchangeIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithStockExchangeJsonPair("")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.listings[0].stockExchange")));
    }

    @Test
    public void shouldRejectCompanyListedWithNullStockExchangeIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithStockExchangeJsonPair("\"stockExchange\":null")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.listings[0].stockExchange")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldRejectCompanyListedWithNonStringStockExchangeIndicatingTypeIsWrong(String stockExchangeValue)
            throws Exception {
        String stockExchange = "\"stockExchange\":" + stockExchangeValue;
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithStockExchangeJsonPair(stockExchange))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.listings[0].stockExchange")));
    }

    @Test
    public void shouldRejectCompanyWithoutTickerIndicatingFieldIsMandatory() throws Exception {
        mockMvc.perform(
                post("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createWithTickerJsonPair("")))
                .andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forMissingFieldAt("$.listings[0].ticker")));
    }

    @Test
    public void shouldRejectCompanyWithNonStringTickerIndicatingTypeIsWrong() throws Exception {
        mockMvc.perform(post("/companies/").contentType(V1_MEDIA_TYPE)
                .content(bodyFactory.createWithTickerJsonPair("\"ticker\":3"))).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson(forStringRequiredAt("$.listings[0].ticker")));
    }
}
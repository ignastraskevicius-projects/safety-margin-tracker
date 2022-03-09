package org.ignast.stockinvesting.quotes.api.controller;

import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.GenericErrorHandlingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.ignast.stockinvesting.quotes.util.test.api.BodySchemaMismatchJsonErrors.*;
import static org.ignast.stockinvesting.quotes.util.test.api.NonExtensibleContentMatchers.contentMatchesJson;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(AppErrorsHandlingConfiguration.class)
abstract class CompanyControllerIntegrationTestBase {
    protected CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    @Autowired
    protected MockMvc mockMvc;

    protected String V1_MEDIA_TYPE = "application/vnd.stockinvesting.quotes-v1.hal+json";

    void rejectsAsBadRequest(String requestBody, String expectedResponse) throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_MEDIA_TYPE)
                        .content(bodyFactory.createWithListingsJsonPair("\"listings\":null")))
                .andExpect(status().isBadRequest()).andExpect(contentMatchesJson(forMissingFieldAt("$.listings")));
    }
}

public class CompanyControllerIntegrationTest extends CompanyControllerIntegrationTestBase {
    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_MEDIA_TYPE)).andExpect(status().isBadRequest())
                .andExpect(contentMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        rejectsAsBadRequest("not-a-json-object", "{\"errorName\":\"bodyNotParsable\"}");
    }

    @Test
    public void shouldDefineCompany() throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_MEDIA_TYPE).content(bodyFactory.createAmazon()))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(put("/companies/").contentType("application/json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(contentMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(put("/companies/").contentType("application/hal+json"))
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
        rejectsAsBadRequest(bodyFactory.createWithoutNameAndId(), forTwoMissingFieldsAt("$.name", "$.id"));
    }
}


class CompanyControllerIdParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @Test
    public void shouldRejectCompanyWithoutIdIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithIdJsonPair(""), forMissingFieldAt("$.id"));
    }

    @Test
    public void shouldRejectCompanyWithNonStringIdIndicatingWrongType() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithIdJsonPair("\"id\":3"), forStringRequiredAt("$.id"));
    }

    @Test
    public void shouldRejectCompaniesWithUnparsableIdAsUUID() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithIdJsonPair("\"id\":\"19c56404-73c6-4cd1-96a4-aae7962b643z\""), forInvalidValueAt("$.id", "Must consist of hyphens (-) and a,b,c,d,e,f and numeric characters only"));
    }
}

class CompanyControllerNameParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @Test
    public void shouldRejectCompanyWithoutNameIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithNameJsonPair(""), forMissingFieldAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithNameAsNonJsonStringIndicatingWrongType() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithNameJsonPair("\"name\":false"), forStringRequiredAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithEmptyName() throws Exception {
        rejectsAsBadRequest(companyWithNameOfLength(0), forInvalidValueAt("$.name", "Company name must be between 1-255 characters"));
    }

    @Test
    public void shouldCreateCompanyWithAtLeast1Character() throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(1)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldRejectCompanyWithTooLongName() throws Exception {
        rejectsAsBadRequest(companyWithNameOfLength(256), forInvalidValueAt("$.name", "Company name must be between 1-255 characters"));
    }

    @Test
    public void shouldCreateCompanyWithWithNamesOfRelativelyReasonableLength() throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_MEDIA_TYPE).content(companyWithNameOfLength(255)))
                .andExpect(status().isCreated());
    }

    private String companyWithNameOfLength(int length) {
        String name = "c".repeat(length);
        return bodyFactory.createWithNameJsonPair(String.format("\"name\":\"%s\"", name));
    }
}

class CompanyControllerListingsParsingIntegrationTest extends CompanyControllerIntegrationTestBase {

    @Test
    public void companyWithoutListingShouldBeRejectedIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithListingsJsonPair(""), forMissingFieldAt("$.listings"));
    }

    @Test
    public void companyWithNonArrayListingShouldBeRejectedIndicatingWrongType() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithListingsJsonPair("\"listings\":3"), forArrayRequiredAt("$.listings"));
    }

    @Test
    public void companyWithZeroListingsShouldBeRejected() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[]"), forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange"));
    }

    @Test
    public void companyWithNullListingShouldBeRejected() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[null]"), forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange"));
    }

    @Test
    public void companyWithIndividualListingAsNonObjectShouldBeRejectedIndicatedWrongType() throws Exception {
        rejectsAsBadRequest("\"listings\":[3.3]", forObjectRequiredAt("$.listings[0]"));
    }

    @Test
    public void shouldNotSupportMultipleListings() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithMultipleListings(), forInvalidValueAt("$.listings", "Multiple listings are not supported"));
    }
}

class CompanyControllerTestIndividualListingParsingIntegrationTest extends CompanyControllerIntegrationTestBase{

    @Test
    public void shouldRejectCompanyListedWithoutMarketIdIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithMarketIdJsonPair(""), forMissingFieldAt("$.listings[0].marketIdentifier"));
    }

    @Test
    public void shouldRejectCompanyListedWithNonStringMarketIdIndicatingTypeIsWrong() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":true"), forStringRequiredAt("$.listings[0].marketIdentifier"));
    }

    @Test
    public void shouldRejectCompanyListedWithInvalidMarketId() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":\"invalid\""), forInvalidValueAt("$.listings[0].marketIdentifier",
                        "Market Identifier is not 4 characters long (ISO 10383 standard)"));
    }

    @Test
    public void shouldRejectCompanyWithoutSymbolIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithSymbolJsonPair(""), forMissingFieldAt("$.listings[0].stockSymbol"));
    }

    @Test
    public void shouldRejectCompanyWithNonStringSymbolIndicatingTypeIsWrong() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":3"), forStringRequiredAt("$.listings[0].stockSymbol"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidSymbol() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":\"TOOLONG\""), forInvalidValueAt("$.listings[0].stockSymbol",
                        "Stock Symbol must contain between 1-5 characters"));
    }
}
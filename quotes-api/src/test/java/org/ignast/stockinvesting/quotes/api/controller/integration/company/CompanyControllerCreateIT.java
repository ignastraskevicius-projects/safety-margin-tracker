package org.ignast.stockinvesting.quotes.api.controller.integration.company;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.junit.jupiter.api.Test;

import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.exchangeNotSupportingAnySymbol;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forArrayRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forIntegerRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forInvalidValueAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forObjectRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forStringRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forTwoMissingFieldsAt;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceContentMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceLinksMatchesJson;
import static org.ignast.stockinvesting.testutil.api.traversor.HateoasLink.link;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public final class CompanyControllerCreateIT extends CompanyControllerITBase {
    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_QUOTES)).andExpect(status().isBadRequest())
                .andExpect(bodyMatchesJson("{\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        rejectsAsBadRequest("not-a-json-object", "{\"errorName\":\"bodyNotParsable\"}");
    }

    @Test
    public void shouldCreateCompany() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        mockMvc.perform(put("/companies/").contentType(V1_QUOTES).content(bodyFactory.createAmazon()))
                .andExpect(status().isCreated()).andExpect(header().string("Content-Type", V1_QUOTES))
                .andExpect(resourceContentMatchesJson(bodyFactory.createAmazon()));
    }

    @Test
    public void createdCompanyShouldContainLinkToItself() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        mockMvc.perform(put("/companies/").contentType(V1_QUOTES).content(bodyFactory.createAmazon()))
                .andExpect(status().isCreated()).andExpect(header().string("Content-Type", V1_QUOTES))
                .andExpect(resourceLinksMatchesJson(link("self", "http://localhost/companies/6")));
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(put("/companies/").contentType("application/json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(bodyMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(put("/companies/").contentType("application/hal+json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(bodyMatchesJson("{\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldIndicateResourceNotReadable() throws Exception {
        mockMvc.perform(get("/companies/").contentType(HAL_JSON)).andExpect(status().isMethodNotAllowed())
                .andExpect(bodyMatchesJson("{\"errorName\":\"methodNotAllowed\"}"));
    }

    @Test
    public void shouldAbleToPreserveErrorsFromMultipleFields() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithoutNameAndId(), forTwoMissingFieldsAt("$.name", "$.id"));
    }
}

final class CompanyControllerIdParsingIT extends CompanyControllerITBase {

    @Test
    public void shouldRejectCompanyWithoutIdIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithIdJsonPair(""), forMissingFieldAt("$.id"));
    }

    @Test
    public void shouldRejectCompanyWithNonIntegerIdIndicatingWrongType() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithIdJsonPair("\"id\":\"nonInteger\""), forIntegerRequiredAt("$.id"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidId() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithIdJsonPair("\"id\":-5"), forInvalidValueAt("$.id", "Must be positive"));
    }
}

final class CompanyControllerNameParsingIT extends CompanyControllerITBase {

    @Test
    public void shouldRejectCompanyWithoutNameIndicatingFieldIsMandatory() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithNameJsonPair(""), forMissingFieldAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithNameAsNonJsonStringIndicatingWrongType() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithNameJsonPair("\"name\":false"), forStringRequiredAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidName() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        rejectsAsBadRequest("", forInvalidValueAt("$.name", "Company name must be between 1-255 characters"));
    }
}

final class CompanyControllerListingsParsingIT extends CompanyControllerITBase {

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
        rejectsAsBadRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[3.3]"), forObjectRequiredAt("$.listings[0]"));
    }

    @Test
    public void shouldNotSupportMultipleListings() throws Exception {
        rejectsAsBadRequest(bodyFactory.createWithMultipleListings(), forInvalidValueAt("$.listings", "Multiple listings are not supported"));
    }
}

final class CompanyControllerTestIndividualListingParsingIT extends CompanyControllerITBase {

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
                        "Stock Symbol must contain between 1-6 characters"));
    }

    @Test
    public void shouldRejectCompanyWithUnsupportedSymbol() throws Exception {
        final val exchange = exchangeNotSupportingAnySymbol();
        when(stockExchanges.getFor(any())).thenReturn(exchange);
        rejectsAsBadRequest(bodyFactory.createAmazon(), "{\"errorName\":\"stockSymbolNotSupportedInThisMarket\"}");
    }
}
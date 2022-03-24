package org.ignast.stockinvesting.quotes.api.controller.integration.company;

import static java.lang.String.format;
import static org.ignast.stockinvesting.quotes.api.controller.integration.company.CompanyControllerRetrievalByIdTest.DOCS_URL;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceContentMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceLinksMatchesJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "documentation.url=" + DOCS_URL })
class CompanyControllerRetrievalByIdTest extends CompanyControllerITBase {

    static final String DOCS_URL = "http://documentation:8081";

    @Test
    public void shouldRetrieveCompany() throws Exception {
        when(companies.findByExternalId(any())).thenReturn(amazon());
        mockMvc
            .perform(get("/companies/5").accept(APP_V1))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(resourceContentMatchesJson(bodyFactory.createAmazon()));
    }

    @Test
    public void retrievedCompanyShouldContainLinks() throws Exception {
        when(companies.findByExternalId(any())).thenReturn(amazon());
        mockMvc
            .perform(get("/companies/5").accept(APP_V1))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(
                resourceLinksMatchesJson(
                    format(
                        """
                           {
                                "_links":{
                                    "self":{"href":"http://localhost/companies/5"},
                                    "quotes:queryQuotedPrice":{"href":"http://localhost/companies/5/price"},
                                    "curies":[{
                                        "name":"quotes",
                                        "href":"%s/rels/quotes/{rel}",
                                        "templated":true
                                    }]
                                }
                            }""",
                        DOCS_URL
                    )
                )
            );
    }

    @Test
    public void shouldNotFindNonexistentCompany() throws Exception {
        when(companies.findByExternalId(any())).thenThrow(CompanyNotFound.class);
        mockMvc
            .perform(get("/companies/5").accept(APP_V1))
            .andExpect(status().isNotFound())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(bodyMatchesJson("{'httpStatus':404}"));
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc
            .perform(get("/companies/5").accept("application/json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(bodyMatchesJson("{\"httpStatus\":406,\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc
            .perform(get("/companies/5").accept("application/hal+json"))
            .andExpect(status().isNotAcceptable())
            .andExpect(bodyMatchesJson("{\"httpStatus\":406,\"errorName\":\"mediaTypeNotAcceptable\"}"));
    }

    @Test
    public void shouldIndicateResourceNotModifiable() throws Exception {
        mockMvc
            .perform(put("/companies/5").contentType(HAL_JSON))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"httpStatus\":405,\"errorName\":\"methodNotAllowed\"}"));
    }
}

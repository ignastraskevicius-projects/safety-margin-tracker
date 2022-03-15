package org.ignast.stockinvesting.quotes.api.controller;

import org.ignast.stockinvesting.quotes.Companies;
import org.ignast.stockinvesting.quotes.StockExchange;
import org.ignast.stockinvesting.quotes.StockExchanges;
import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.util.test.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({AppErrorsHandlingConfiguration.class, HalConfig.class})
abstract class CompanyControllerITBase {
    protected CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    @MockBean
    protected Companies companies;

    @MockBean
    protected StockExchanges stockExchanges;

    @Autowired
    protected MockMvc mockMvc;

    protected String V1_QUOTES = "application/vnd.stockinvesting.quotes-v1.hal+json";

    void rejectsAsBadRequest(String requestBody, String expectedResponse) throws Exception {
        mockMvc.perform(put("/companies/").contentType(V1_QUOTES)
                        .content(requestBody))
                .andExpect(status().isBadRequest()).andExpect(bodyMatchesJson(expectedResponse));
    }
}

class CompanyControllerITBaseTest extends CompanyControllerITBase {

    @Test
    public void shouldNotRejectGoodRequest()  {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> rejectsAsBadRequest(bodyFactory.createAmazon(), "shouldNotBeBadRequest"))
                .withMessage("Status expected:<400> but was:<201>");
    }
}


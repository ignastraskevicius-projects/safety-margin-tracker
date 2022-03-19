package org.ignast.stockinvesting.quotes.api.controller.integration.company;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.RequiredArgsConstructor;
import org.ignast.stockinvesting.quotes.api.controller.HalConfig;
import org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppErrorsHandlingConfiguration;
import org.ignast.stockinvesting.quotes.domain.Companies;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest
@Import({ AppErrorsHandlingConfiguration.class, HalConfig.class })
abstract class CompanyControllerITBase {

    protected static final String APP_V1 = "application/vnd.stockinvesting.quotes-v1.hal+json";

    protected CompanyJsonBodyFactory bodyFactory = new CompanyJsonBodyFactory();

    @MockBean
    protected Companies companies;

    @MockBean
    protected StockExchanges stockExchanges;

    @Autowired
    protected MockMvc mockMvc;

    MockMvcAssert assertThatRequest(final String body) throws Exception {
        return new MockMvcAssert(mockMvc.perform(put("/companies/").contentType(APP_V1).content(body)));
    }

    @RequiredArgsConstructor
    static final class MockMvcAssert {

        private final ResultActions mockMvcResult;

        void failsValidation(final String expectedResponse) throws Exception {
            mockMvcResult.andExpect(status().isBadRequest()).andExpect(bodyMatchesJson(expectedResponse));
        }
    }
}

final class CompanyControllerITBaseTest extends CompanyControllerITBase {

    @Test
    public void shouldNotRejectGoodRequest() {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> assertThatRequest(bodyFactory.createAmazon()).failsValidation("any"))
            .withMessage("Status expected:<400> but was:<201>");
    }

    @Test
    public void shouldRejectBadRequest() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("")).failsValidation(forMissingFieldAt("$.id"));
    }
}

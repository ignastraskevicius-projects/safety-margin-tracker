package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class ControllerAdviceForBusinessErrorsTest {
    private static final ControllerAdviceForBusinessErrors ERROR_HANDLER = new ControllerAdviceForBusinessErrors();

    @Test
    public void shouldHandleSymbolNotSupported() {
        assertThat(ERROR_HANDLER.handleCompanyNotFound(mock(CompanyNotFound.class)).getErrorName()).isNull();
    }

    @Test
    public void shouldHandleSymbolNotSupportedInMarket() {
        assertThat(ERROR_HANDLER.handleSymbolNotSupportedInMarket(mock(StockSymbolNotSupportedInThisMarket.class))
                .getErrorName()).isEqualTo("stockSymbolNotSupportedInThisMarket");
    }


}
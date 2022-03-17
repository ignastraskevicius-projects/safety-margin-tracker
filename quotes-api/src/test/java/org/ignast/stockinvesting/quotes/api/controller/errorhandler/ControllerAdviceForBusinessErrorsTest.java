package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class ControllerAdviceForBusinessErrorsTest {
    private ControllerAdviceForBusinessErrors errorHandler = new ControllerAdviceForBusinessErrors();

    @Test
    public void shouldHandleSymbolNotSupported() {
        assertThat(errorHandler.handleCompanyNotFound(mock(CompanyNotFound.class)).getErrorName()).isNull();
    }

    @Test
    public void shouldHandleSymbolNotSupportedInMarket() {
        assertThat(errorHandler.handleSymbolNotSupportedInMarket(mock(StockSymbolNotSupportedInThisMarket.class))
                .getErrorName()).isEqualTo("stockSymbolNotSupportedInThisMarket");
    }


}
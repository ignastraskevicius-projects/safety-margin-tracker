package org.ignast.stockinvesting.api.controller.errorhandler;

import org.ignast.stockinvesting.estimates.domain.StockSymbolNotSupported;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ControllerAdviceForBusinessErrorsTest {
    private ControllerAdviceForBusinessErrors errorHandler = new ControllerAdviceForBusinessErrors();
    @Test
    public void shouldHandleSymbolNotSupported() {
        assertThat(errorHandler.handleStockSymbolNotSupported(mock(StockSymbolNotSupported.class)).getErrorName())
                .isEqualTo("stockSymbolNotSupported");
    }


}
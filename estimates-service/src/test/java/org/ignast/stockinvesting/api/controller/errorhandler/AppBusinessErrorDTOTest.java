package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppBusinessErrorDTOTest {

    @Test
    public void shouldCreateStockSymbolNotSupported() {
        AppBusinessErrorDTO error = AppBusinessErrorDTO.createForStockSymbolNotSupported();

        assertThat(error.getErrorName()).isEqualTo("stockSymbolNotSupported");
    }
}
package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.val;
import org.junit.jupiter.api.Test;

public final class AppBusinessErrorDTOTest {

    @Test
    public void shouldCreateForCompanyNotFound() {
        final val error = AppBusinessErrorDTO.createForCompanyNotFound();

        assertThat(error.getErrorName()).isNull();
    }

    @Test
    public void shouldCreateForStockSymbolNotSupportedInTheMarket() {
        final val error = AppBusinessErrorDTO.createForStockSymbolNotSupportedInThisMarket();

        assertThat(error.getErrorName()).isEqualTo("stockSymbolNotSupportedInThisMarket");
    }
}

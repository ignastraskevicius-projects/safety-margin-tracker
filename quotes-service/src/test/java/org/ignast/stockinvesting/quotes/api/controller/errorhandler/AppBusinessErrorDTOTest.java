package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppBusinessErrorDTOTest {

    @Test
    public void shouldCreateCompanyNotFound() {
        AppBusinessErrorDTO error = AppBusinessErrorDTO.createForCompanyNotFound();

        assertThat(error.getErrorName()).isNull();
    }
}
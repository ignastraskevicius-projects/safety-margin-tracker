package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StandardErrorDTOTest {

    @Test
    public void shouldCreateUnknownError() {
        StandardErrorDTO error = StandardErrorDTO.createUnknownError();

        assertThat(error.getErrorName()).isEqualTo("unknownError");
    }
}
package org.ignast.stockinvesting.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        assertThat(new Company()).isEqualTo(new Company());
        assertThat(new Company().hashCode()).isEqualTo(new Company().hashCode());
    }
}
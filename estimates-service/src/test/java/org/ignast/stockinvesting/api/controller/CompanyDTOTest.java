package org.ignast.stockinvesting.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.val;
import org.junit.jupiter.api.Test;

final class CompanyDTOTest {

    @Test
    public void shouldPreserveNonNestedAttributes() {
        final val company = new CompanyDTO("someId", "Amazon", "US", "USD");
        assertThat(company.getId()).isEqualTo("someId");
        assertThat(company.getHomeCountry()).isEqualTo("US");
        assertThat(company.getName()).isEqualTo("Amazon");
        assertThat(company.getFunctionalCurrency()).isEqualTo("USD");
    }

    @Test
    public void shouldAllowNullFieldsToEnableJavaxValidation() {
        final val company = new CompanyDTO(null, null, null, null);
        assertThat(company.getId()).isNull();
        assertThat(company.getName()).isNull();
    }
}

package org.ignast.stockinvesting.api.controller;

import lombok.val;
import org.ignast.stockinvesting.domain.Companies;
import org.ignast.stockinvesting.estimates.domain.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CompanyControllerTest {
    private final Companies companies = mock(Companies.class);

    private CompanyController controller = new CompanyController(companies);

    @ParameterizedTest
    @ValueSource(strings = { "invalidCurrency", "anotherInvalidCurrency" })
    public void shouldFailToCreateCompanyWithInvalidCurrency(String currency) {
        val company = new CompanyDTO("anyName", "anyHomeCountry", currency, Collections.emptyList());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> controller.defineCompany(company));
    }

    @Test
    public void shouldCreateCompany() {
        val currencyCode = "USD";
        val company = new CompanyDTO("Santander", "FR", currencyCode, Collections.emptyList());
        val captor = ArgumentCaptor.forClass(Company.class);

        controller.defineCompany(company);

        verify(companies).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new Company("Santander", "FR", Currency.getInstance(currencyCode)));
    }

}
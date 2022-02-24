package org.ignast.stockinvesting.api.controller;

import lombok.val;
import org.ignast.stockinvesting.domain.Companies;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CompanyControllerTest {
    private final Companies companies = mock(Companies.class);

    private CompanyController controller = new CompanyController(companies);

    @ParameterizedTest
    @ValueSource(strings = { "invalidCurrency", "anotherInvalidCurrency" })
    public void shouldFailToCreateCompanyIfCurrencyIsWrong(String currency) {
        val company = new CompanyDTO("anyName", "anyHomeCountry", currency, Collections.emptyList());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> controller.defineCompany(company));
    }

    @ParameterizedTest
    @ValueSource(strings = { "EUR", "USD" })
    public void shouldCreateCompanyWithEurAndUsd(String currency) {
        val company = new CompanyDTO("anyName", "anyHomeCountry", currency, Collections.emptyList());

        controller.defineCompany(company);

        verify(companies).create();
    }

}
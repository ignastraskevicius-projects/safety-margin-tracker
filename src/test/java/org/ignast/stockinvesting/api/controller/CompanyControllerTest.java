package org.ignast.stockinvesting.api.controller;

import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CompanyControllerTest {
    private CompanyController controller = new CompanyController();

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
    }

}
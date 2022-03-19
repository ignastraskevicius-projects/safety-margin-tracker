package org.ignast.stockinvesting.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Currency;
import lombok.val;
import org.ignast.stockinvesting.domain.Companies;
import org.ignast.stockinvesting.estimates.domain.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

final class CompanyControllerTest {

    private final Companies companies = mock(Companies.class);

    private final CompanyController controller = new CompanyController(companies);

    @ParameterizedTest
    @ValueSource(strings = { "invalidCurrency", "anotherInvalidCurrency" })
    public void shouldFailToCreateCompanyWithInvalidCurrency(final String currency) {
        final val company = new CompanyDTO("anyId", "anyName", "anyHomeCountry", currency);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> controller.defineCompany(company));
    }

    @Test
    public void shouldCreateCompany() {
        final val currencyCode = "USD";
        final val company = new CompanyDTO("someId", "Santander", "FR", currencyCode);
        final val captor = ArgumentCaptor.forClass(Company.class);

        controller.defineCompany(company);

        verify(companies).create(captor.capture());
        assertThat(captor.getValue())
            .isEqualTo(new Company("Santander", "FR", Currency.getInstance(currencyCode)));
    }
}

package org.ignast.stockinvesting.quotes.api.controller;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Companies;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.testutil.MockitoUtils;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

final class PriceControllerTest {

    private static final Money ONE_USD = Money.of(ONE, "USD");

    @Test
    public void shouldNotBeCreatedWithNullArgument() {
        assertThatNullPointerException().isThrownBy(() -> new PriceController(null));
        new PriceController(mock(Companies.class));
    }

    @Test
    public void shouldRetrieveQuotedPriceOfCompany() {
        final val companyId = any();
        final val companies = mock(Companies.class);
        final val company = MockitoUtils.mock(
            Company.class,
            c -> when(c.getQuotedPrice()).thenReturn(ONE_USD)
        );
        when(companies.findByExternalId(new CompanyExternalId(companyId))).thenReturn(company);
        final val controller = new PriceController(companies);

        final val priceDto = controller.retrievePriceForCompanyWithId(companyId);

        assertThat(priceDto.getAmount()).isEqualTo("1");
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private int any() {
        return 6;
    }
}

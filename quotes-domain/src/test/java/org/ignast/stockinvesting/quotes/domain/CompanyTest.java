package org.ignast.stockinvesting.quotes.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.domain.DomainFactoryForTests.anyCompanyName;
import static org.ignast.stockinvesting.quotes.domain.DomainFactoryForTests.anyId;
import static org.ignast.stockinvesting.quotes.domain.DomainFactoryForTests.anyStockExchange;
import static org.ignast.stockinvesting.quotes.domain.DomainFactoryForTests.anySymbol;
import static org.ignast.stockinvesting.testutil.MockitoUtils.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

public final class CompanyTest {

    @Test
    public void shouldFailToBeCreatedWithUnsupportedStockSymbols() {
        final val symbol = new StockSymbol("NONO");
        final val exchange = mock(
            StockExchange.class,
            e -> when(e.getQuotedPrice(symbol)).thenThrow(StockSymbolNotSupportedInThisMarket.class)
        );
        assertThatExceptionOfType(StockSymbolNotSupportedInThisMarket.class)
            .isThrownBy(() -> Company.create(anyId(), anyCompanyName(), symbol, exchange).getExternalId());
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 5 })
    public void shouldHaveExternalId(final int externalId) {
        assertThat(
            Company
                .create(new CompanyExternalId(externalId), anyCompanyName(), anySymbol(), anyStockExchange())
                .getExternalId()
        )
            .isEqualTo(new CompanyExternalId(externalId));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Microsoft" })
    public void shouldHaveName(final String name) {
        assertThat(Company.create(anyId(), new CompanyName(name), anySymbol(), anyStockExchange()).getName())
            .isEqualTo(new CompanyName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldHaveStockSymbol(final String symbol) {
        assertThat(
            Company
                .create(anyId(), anyCompanyName(), new StockSymbol(symbol), anyStockExchange())
                .getStockSymbol()
        )
            .isEqualTo(new StockSymbol(symbol));
    }

    @Test
    public void shouldHaveStockExchange() {
        final val stockExchange = Mockito.mock(StockExchange.class);
        assertThat(Company.create(anyId(), anyCompanyName(), anySymbol(), stockExchange).getStockExchange())
            .isEqualTo(stockExchange);
    }

    @Test
    public void shouldConsultStockExchangeForPrice() {
        final StockSymbol symbol = new StockSymbol("AMZN");
        final val tenUsd = Money.of(BigDecimal.TEN, "USD");
        final val stockExchange = mock(
            StockExchange.class,
            e -> when(e.getQuotedPrice(symbol)).thenReturn(tenUsd)
        );
        final val company = Company.create(anyId(), anyCompanyName(), symbol, stockExchange);

        assertThat(company.getQuotedPrice()).isEqualTo(tenUsd);
    }

    @Test
    public void shouldNotHaveNullField() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Company.create(null, anyCompanyName(), anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Company.create(anyId(), null, anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Company.create(anyId(), anyCompanyName(), null, anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Company.create(anyId(), anyCompanyName(), anySymbol(), null));
    }
}

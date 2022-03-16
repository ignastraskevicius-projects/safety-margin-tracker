package org.ignast.stockinvesting.quotes.domain;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.domain.DomainFactoryForTests.*;
import static org.ignast.stockinvesting.testutil.testutil.MockitoUtils.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(Company.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @Test
    public void shouldFailToBeCreatedWithUnsupportedStockSymbols() {
        val symbol = new StockSymbol("NONO");
        val exchange = mock(StockExchange.class, e -> when(e.getQuotedPrice(symbol)).thenThrow(StockSymbolNotSupportedInThisMarket.class));
        assertThatExceptionOfType(StockSymbolNotSupportedInThisMarket.class).isThrownBy(() ->
                Company.create(anyId(), anyCompanyName(), symbol, exchange).getExternalId());
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 5 })
    public void shouldHaveExternalId(int externalId) {
        assertThat(Company.create(new CompanyExternalId(externalId), anyCompanyName(), anySymbol(), anyStockExchange()).getExternalId()).isEqualTo(new CompanyExternalId(externalId));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Microsoft" })
    public void shouldHaveName(String name) {
        assertThat(Company.create(anyId(), new CompanyName(name), anySymbol(), anyStockExchange()).getName()).isEqualTo(new CompanyName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldHaveStockSymbol(String symbol) {
        assertThat(Company.create(anyId(), anyCompanyName(), new StockSymbol(symbol), anyStockExchange()).getStockSymbol()).isEqualTo(new StockSymbol(symbol));
    }

    @Test
    public void shouldHaveStockExchange() {
        val stockExchange =  Mockito.mock(StockExchange.class);
        assertThat(Company.create(anyId(), anyCompanyName(), anySymbol(), stockExchange).getStockExchange()).isEqualTo(stockExchange);
    }

    @Test
    public void shouldConsultStockExchangeForPrice() {
        StockSymbol symbol = new StockSymbol("AMZN");
        val tenUsd = Money.of(BigDecimal.TEN, "USD");
        val stockExchange = mock(StockExchange.class, e -> when(e.getQuotedPrice(symbol)).thenReturn(tenUsd));
        val company = Company.create(anyId(), anyCompanyName(), symbol, stockExchange);

        assertThat(company.getQuotedPrice()).isEqualTo(tenUsd);
    }

    @Test
    public void shouldNotHaveNullField() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Company.create(null, anyCompanyName(), anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Company.create(anyId(), null, anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Company.create(anyId(), anyCompanyName(), null, anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Company.create(anyId(), anyCompanyName(), anySymbol(), null));
    }
}
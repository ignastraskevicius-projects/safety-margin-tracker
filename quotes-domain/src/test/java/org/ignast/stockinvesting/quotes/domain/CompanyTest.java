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
import static org.mockito.Mockito.when;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(Company.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 5 })
    public void shouldHaveExternalId(int externalId) {
        assertThat(new Company(new CompanyExternalId(externalId), anyCompanyName(), anySymbol(), anyStockExchange()).getExternalId()).isEqualTo(new CompanyExternalId(externalId));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Microsoft" })
    public void shouldHaveName(String name) {
        assertThat(new Company(anyId(), new CompanyName(name), anySymbol(), anyStockExchange()).getName()).isEqualTo(new CompanyName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldHaveStockSymbol(String symbol) {
        assertThat(new Company(anyId(), anyCompanyName(), new StockSymbol(symbol), anyStockExchange()).getStockSymbol()).isEqualTo(new StockSymbol(symbol));
    }

    @Test
    public void shouldHaveStockExchange() {
        val stockExchange =  Mockito.mock(StockExchange.class);
        assertThat(new Company(anyId(), anyCompanyName(), anySymbol(), stockExchange).getStockExchange()).isEqualTo(stockExchange);
    }

    @Test
    public void shouldConsultStockExchangeForPrice() {
        StockSymbol symbol = new StockSymbol("AMZN");
        val tenUsd = Money.of(BigDecimal.TEN, "USD");
        val stockExchange = mock(StockExchange.class, e -> when(e.getQuotedPrice(symbol)).thenReturn(tenUsd));
        val company = new Company(anyId(), anyCompanyName(), symbol, stockExchange);

        assertThat(company.getQuotedPrice()).isEqualTo(tenUsd);
    }

    @Test
    public void shouldNotHaveNullField() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(null, anyCompanyName(), anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(anyId(), null, anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(anyId(), anyCompanyName(), null, anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(anyId(), anyCompanyName(), anySymbol(), null));
    }
}
package org.ignast.stockinvesting.quotes;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.DomainFactoryForTests.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(Company.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @ParameterizedTest
    @ValueSource(strings = { "55f20234-0f0f-4d61-ae84-44e5428e17c1", "92409239-a285-4cb9-9375-9122a14afd17" })
    public void shouldHaveId(String id) {
        assertThat(new Company(CompanyId.toUUID(id), anyCompanyName(), anySymbol(), anyStockExchange()).getId()).isEqualTo(UUID.fromString(id));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Microsoft" })
    public void shouldHaveName(String name) {
        assertThat(new Company(anyId(), new CompanyName(name), anySymbol(), anyStockExchange()).getName()).isEqualTo(new CompanyName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldHaveStockSymbol(String symbol) {
        assertThat(new Company(anyId(), anyCompanyName(), new StockSymbol(symbol), anyStockExchange()).getSockSymbol()).isEqualTo(new StockSymbol(symbol));
    }

    @ParameterizedTest
    @ValueSource(strings = { "XNYS", "XASX" })
    public void shouldHaveStockExchange(String micStr) {
        val quotes = mock(QuotesRepository.class);
        val mic = new MarketIdentifierCode(micStr);
        StockSymbol symbol = new StockSymbol("AMZN");
        when(quotes.getQuotedPriceOf(symbol, mic)).thenReturn(BigDecimal.TEN);
        val stockExchange = StockExchange.create(mic, new CurrencyCode("USD"), quotes);

        val company = new Company(anyId(), anyCompanyName(), symbol, stockExchange);

        assertThat(company.getQuotedPrice()).isEqualTo(Money.of(BigDecimal.TEN, "USD"));
    }

    @Test
    public void shouldNotHaveNullField() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(null, new CompanyName("name"), anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(anyId(), null, anySymbol(), anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(anyId(), new CompanyName("name"), null, anyStockExchange()));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(anyId(), new CompanyName("name"), anySymbol(), null));
    }
}
package org.ignast.stockinvesting.quotes;

import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockExchangeTest {

    private QuotesRepository quotes = mock(QuotesRepository.class);

    @Test
    public void shouldNotBeInitializedWithNulls() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new StockExchange(null, new CurrencyCode("USD"), quotes));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new StockExchange(new MarketIdentifierCode("XNYS"), null, quotes));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new StockExchange(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), null));
    }

    @Test
    public void shouldNotCalculatePriceForNullSymbol() {
        when(quotes.getQuotedPriceOf(any(), any())).thenReturn(ZERO);

        val stockExchange = new StockExchange(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> stockExchange.getQuotedPrice(null));
    }

    @Test
    public void shouldFailOnNullPrice() {
        when(quotes.getQuotedPriceOf(any(), any())).thenThrow(NullPointerException.class);
        val stockExchange = new StockExchange(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> stockExchange.getQuotedPrice(new StockSymbol("AMZN")));
    }

    @Test
    public void shouldGetQuotedPriceOfNyseListedCompany() {
        val amazonSymbol = new StockSymbol("AMZN");
        val nyseMic = new MarketIdentifierCode("XNYS");
        when(quotes.getQuotedPriceOf(amazonSymbol, nyseMic)).thenReturn(TEN);

        shouldGetQuotedPriceOfListedCompany(amazonSymbol, nyseMic, TEN, "USD");
    }

    @Test
    public void shouldGetQuotedPriceOfSseListedCompany() {
        val bankOfChinaSymbol = new StockSymbol("601988");
        val sseMic = new MarketIdentifierCode("XSHG");
        BigDecimal priceNumericAmount = BigDecimal.valueOf(3);
        when(quotes.getQuotedPriceOf(bankOfChinaSymbol, sseMic)).thenReturn(priceNumericAmount);

        shouldGetQuotedPriceOfListedCompany(bankOfChinaSymbol, sseMic, priceNumericAmount, "CNY");
    }

    private void shouldGetQuotedPriceOfListedCompany(StockSymbol symbol, MarketIdentifierCode mic, BigDecimal quotedPrice, String quoteCurrency) {
        val exchange = new StockExchange(mic, new CurrencyCode(quoteCurrency), quotes);

        Money price = exchange.getQuotedPrice(symbol);

        assertThat(price).isEqualTo(Money.of(quotedPrice, quoteCurrency));
    }

}
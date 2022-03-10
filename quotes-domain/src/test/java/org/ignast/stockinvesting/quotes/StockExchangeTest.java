package org.ignast.stockinvesting.quotes;

import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.StockExchange.create;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockExchangeTest {

    private QuotesRepository quotes = mock(QuotesRepository.class);

    @Test
    public void shouldNotBeInitializedWithNulls() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> create(null, new CurrencyCode("USD"), quotes));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> create(new MarketIdentifierCode("XNYS"), null, quotes));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> create(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), null));
    }

    @Test
    public void shouldNotCalculatePriceForNullSymbol() {
        when(quotes.getQuotedPriceOf(any(), any())).thenReturn(ZERO);

        val stockExchange = create(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> stockExchange.getQuotedPrice(null));
    }

    @Test
    public void shouldFailOnNullPrice() {
        when(quotes.getQuotedPriceOf(any(), any())).thenThrow(NullPointerException.class);
        val stockExchange = create(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> stockExchange.getQuotedPrice(new StockSymbol("AMZN")));
    }

    @Test
    public void shouldGetQuotedPriceForNyseListedCompany() {
        val amazonSymbol = new StockSymbol("AMZN");
        val nyseMic = new MarketIdentifierCode("XNYS");
        when(quotes.getQuotedPriceOf(amazonSymbol, nyseMic)).thenReturn(TEN);

        shouldGetQuotedPriceOfListedCompany(amazonSymbol, nyseMic, TEN, "USD");
    }

    @Test
    public void shouldGetQuotedPriceForSseListedCompany() {
        val bankOfChinaSymbol = new StockSymbol("601988");
        val sseMic = new MarketIdentifierCode("XSHG");
        BigDecimal priceNumericAmount = BigDecimal.valueOf(3);
        when(quotes.getQuotedPriceOf(bankOfChinaSymbol, sseMic)).thenReturn(priceNumericAmount);

        shouldGetQuotedPriceOfListedCompany(bankOfChinaSymbol, sseMic, priceNumericAmount, "CNY");
    }

    private void shouldGetQuotedPriceOfListedCompany(StockSymbol symbol, MarketIdentifierCode mic, BigDecimal quotedPrice, String quoteCurrency) {
        val exchange = create(mic, new CurrencyCode(quoteCurrency), quotes);

        Money price = exchange.getQuotedPrice(symbol);

        assertThat(price).isEqualTo(Money.of(quotedPrice, quoteCurrency));
    }

}

class LondonStockExchangeTest {

    private QuotesRepository quotes = mock(QuotesRepository.class);

    private final StockExchange lse = create(new MarketIdentifierCode("XLON"), new CurrencyCode("GBP"), quotes);

    private final StockSymbol astrazenecaSymbol = new StockSymbol("AZN");

    private final MarketIdentifierCode lseMic = new MarketIdentifierCode("XLON");

    @Test
    public void shouldConvertQuotedPriceFromPenceToGbpForCompanyListedInLse() {
        BigDecimal priceNumericAmount = new BigDecimal("9121.0000");
        when(quotes.getQuotedPriceOf(astrazenecaSymbol, lseMic)).thenReturn(priceNumericAmount);

        Money price = lse.getQuotedPrice(astrazenecaSymbol);

        assertThat(price).isEqualTo(Money.of(new BigDecimal("91.210000"), "GBP"));
    }

    @Test
    public void shouldNotBeInitializedWithNulls() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> create(null, new CurrencyCode("GBP"), quotes));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> create(new MarketIdentifierCode("XLON"), null, quotes));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> create(new MarketIdentifierCode("XLON"), new CurrencyCode("GBP"), null));
    }

    @Test
    public void shouldThrowForNonGbpCurrency() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> create(new MarketIdentifierCode("XLON"), new CurrencyCode("EUR"), quotes))
                .withMessage("'EUR' currency is not supported in stock exchange identified with market identifier 'XLON'");
    }

    @Test
    public void shouldNotCalculatePriceForNullSymbol() {
        when(quotes.getQuotedPriceOf(any(), any())).thenReturn(ZERO);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> lse.getQuotedPrice(null));
    }

    @Test
    public void shouldFailOnNullPrice() {
        when(quotes.getQuotedPriceOf(any(), any())).thenThrow(NullPointerException.class);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> lse.getQuotedPrice(new StockSymbol("AMZN")));
    }
}
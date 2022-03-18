package org.ignast.stockinvesting.quotes.domain;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.domain.StockExchange.create;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class StockExchangeTest {

    private final QuotesRepository quotes = mock(QuotesRepository.class);

    @Test
    public void shouldNotBeInitializedWithNulls() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> create(null, new CurrencyCode("USD"), quotes));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> create(new MarketIdentifierCode("XNYS"), null, quotes));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> create(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), null));
    }

    @Test
    public void shouldNotCalculatePriceForNullSymbol() {
        when(quotes.getQuotedPriceOf(any(), any())).thenReturn(ZERO);

        final val stockExchange = create(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes);

        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> stockExchange.getQuotedPrice(null));
    }

    @Test
    public void shouldFailOnNullPrice() {
        when(quotes.getQuotedPriceOf(any(), any())).thenReturn(null);
        final val stockExchange = create(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes);

        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> stockExchange.getQuotedPrice(new StockSymbol("AMZN")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "XNYS", "XNAS" })
    public void shouldPreserveMarketIdentifierCode(final String micStr) {
        final val mic = new MarketIdentifierCode(micStr);

        assertThat(create(mic, new CurrencyCode("USD"), quotes).getMarketIdentifierCode()).isEqualTo(mic);
    }

    @Test
    public void shouldGetQuotedPriceForNyseListedCompany() {
        final val amazonSymbol = new StockSymbol("AMZN");
        final val nyseMic = new MarketIdentifierCode("XNYS");
        when(quotes.getQuotedPriceOf(amazonSymbol, nyseMic)).thenReturn(TEN);

        shouldGetQuotedPriceOfListedCompany(amazonSymbol, nyseMic, TEN, "USD");
    }

    @Test
    public void shouldGetQuotedPriceForSseListedCompany() {
        final val bankOfChinaSymbol = new StockSymbol("601988");
        final val sseMic = new MarketIdentifierCode("XSHG");
        final val priceNumericAmount = BigDecimal.valueOf(3);
        when(quotes.getQuotedPriceOf(bankOfChinaSymbol, sseMic)).thenReturn(priceNumericAmount);

        shouldGetQuotedPriceOfListedCompany(bankOfChinaSymbol, sseMic, priceNumericAmount, "CNY");
    }

    private void shouldGetQuotedPriceOfListedCompany(
        final StockSymbol symbol,
        final MarketIdentifierCode mic,
        final BigDecimal quotedPrice,
        final String quoteCurrency
    ) {
        final val exchange = create(mic, new CurrencyCode(quoteCurrency), quotes);

        final val price = exchange.getQuotedPrice(symbol);

        assertThat(price).isEqualTo(Money.of(quotedPrice, quoteCurrency));
    }
}

final class LondonStockExchangeTest {

    private final QuotesRepository quotes = mock(QuotesRepository.class);

    private final StockExchange lse = create(
        new MarketIdentifierCode("XLON"),
        new CurrencyCode("GBP"),
        quotes
    );

    private final StockSymbol astrazenecaSymbol = new StockSymbol("AZN");

    private final MarketIdentifierCode lseMic = new MarketIdentifierCode("XLON");

    @Test
    public void shouldConvertQuotedPriceFromPenceToGbpForCompanyListedInLse() {
        final val priceNumericAmount = new BigDecimal("9121.0000");
        when(quotes.getQuotedPriceOf(astrazenecaSymbol, lseMic)).thenReturn(priceNumericAmount);

        final val price = lse.getQuotedPrice(astrazenecaSymbol);

        assertThat(price).isEqualTo(Money.of(new BigDecimal("91.210000"), "GBP"));
    }

    @Test
    public void shouldNotBeInitializedWithNulls() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> create(null, new CurrencyCode("GBP"), quotes));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> create(new MarketIdentifierCode("XLON"), null, quotes));
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> create(new MarketIdentifierCode("XLON"), new CurrencyCode("GBP"), null));
    }

    @Test
    public void shouldThrowForNonGbpCurrency() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> create(new MarketIdentifierCode("XLON"), new CurrencyCode("EUR"), quotes))
            .withMessage(
                "'EUR' currency is not supported in stock exchange identified with market identifier 'XLON'"
            );
    }

    @Test
    public void shouldNotCalculatePriceForNullSymbol() {
        when(quotes.getQuotedPriceOf(any(), any())).thenReturn(ZERO);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> lse.getQuotedPrice(null));
    }

    @Test
    public void shouldFailOnNullPrice() {
        when(quotes.getQuotedPriceOf(any(), any())).thenThrow(NullPointerException.class);

        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> lse.getQuotedPrice(new StockSymbol("AMZN")));
    }
}

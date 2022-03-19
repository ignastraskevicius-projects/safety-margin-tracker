package org.ignast.stockinvesting.quotes.domain;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public final class StockExchangesTest {

    private final QuotesRepository quotes = mock(QuotesRepository.class);

    private final StockExchanges stockExchanges = new StockExchanges(quotes);

    @Test
    public void FrankfurtExchangeShouldBeSupported() {
        final val volkswagenSymbol = new StockSymbol("VOW3");
        final val fraMic = new MarketIdentifierCode("XFRA");
        when(quotes.getQuotedPriceOf(volkswagenSymbol, fraMic)).thenReturn(TEN);

        final val price = stockExchanges.getFor(fraMic).getQuotedPrice(volkswagenSymbol);

        assertThat(price).isEqualTo(Money.of(TEN, "EUR"));
    }

    @Test
    public void NewYorkExchangeShouldBeSupported() {
        final val amazonSymbol = new StockSymbol("AMZN");
        final val nyseMic = new MarketIdentifierCode("XNYS");
        when(quotes.getQuotedPriceOf(amazonSymbol, nyseMic)).thenReturn(ONE);

        final val price = stockExchanges.getFor(nyseMic).getQuotedPrice(amazonSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "USD"));
    }

    @Test
    public void TorontoExchangeShouldBeSupported() {
        final val royalBankOfCanadaSymbol = new StockSymbol("RY");
        final val tsxMic = new MarketIdentifierCode("XTSE");
        when(quotes.getQuotedPriceOf(royalBankOfCanadaSymbol, tsxMic)).thenReturn(ONE);

        final val price = stockExchanges.getFor(tsxMic).getQuotedPrice(royalBankOfCanadaSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "CAD"));
    }

    @Test
    public void HongKongStockExchangeShouldBeSupported() {
        final val alibabaSymbol = new StockSymbol("9988");
        final val hkexMic = new MarketIdentifierCode("XHKG");
        when(quotes.getQuotedPriceOf(alibabaSymbol, hkexMic)).thenReturn(ONE);

        final val price = stockExchanges.getFor(hkexMic).getQuotedPrice(alibabaSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "HKD"));
    }

    @Test
    public void AustralianSecuritiesExchangeShouldBeSupported() {
        final val telsraSymbol = new StockSymbol("TLS");
        final val asxMic = new MarketIdentifierCode("XASX");
        when(quotes.getQuotedPriceOf(telsraSymbol, asxMic)).thenReturn(ONE);

        final val price = stockExchanges.getFor(asxMic).getQuotedPrice(telsraSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "AUD"));
    }

    @Test
    public void NasdaqShouldBeSupported() {
        final val appleSymbol = new StockSymbol("AAPL");
        final val nasdaqMic = new MarketIdentifierCode("XNAS");
        when(quotes.getQuotedPriceOf(appleSymbol, nasdaqMic)).thenReturn(ONE);

        final val price = stockExchanges.getFor(nasdaqMic).getQuotedPrice(appleSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "USD"));
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void LondonStockExchangeShouldBeSupported() {
        final val penceInPound = 100;
        final val astrazenecaSymbol = new StockSymbol("AZN");
        final val lseMic = new MarketIdentifierCode("XLON");
        when(quotes.getQuotedPriceOf(astrazenecaSymbol, lseMic)).thenReturn(new BigDecimal(penceInPound));

        final val price = stockExchanges.getFor(lseMic).getQuotedPrice(astrazenecaSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "GBP"));
    }

    @Test
    public void shouldThrowForNotSupportedStockExchanges() {
        final val madridMic = new MarketIdentifierCode("XMAD");

        assertThatExceptionOfType(StockExchangeNotSupported.class)
            .isThrownBy(() -> stockExchanges.getFor(madridMic))
            .withMessage("Market Identifier Code 'XMAD' is not supported")
            .isInstanceOf(ApplicationException.class);
    }

    @Test
    public void shouldNotSupportNullMarketIdentifierCodes() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> stockExchanges.getFor(null));
    }
}

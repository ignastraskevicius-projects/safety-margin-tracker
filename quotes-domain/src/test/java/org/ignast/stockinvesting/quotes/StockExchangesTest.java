package org.ignast.stockinvesting.quotes;

import lombok.val;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockExchangesTest {
    private QuotesRepository quotes = mock(QuotesRepository.class);

    private StockExchanges stockExchanges = new StockExchanges(quotes);

    @Test
    public void FrankfurtExchangeShouldBeSupported() {
        val volkswagenSymbol = new StockSymbol("VOW3");
        val fraMic = new MarketIdentifierCode("XFRA");
        when(quotes.getQuotedPriceOf(volkswagenSymbol, fraMic)).thenReturn(TEN);

        Money price = stockExchanges.getFor(fraMic).getQuotedPrice(volkswagenSymbol);

        assertThat(price).isEqualTo(Money.of(TEN, "EUR"));
    }

    @Test
    public void NewYorkExchangeShouldBeSupported() {
        val amazonSymbol = new StockSymbol("AMZN");
        val nyseMic = new MarketIdentifierCode("XNYS");
        when(quotes.getQuotedPriceOf(amazonSymbol, nyseMic)).thenReturn(ONE);

        Money price = stockExchanges.getFor(nyseMic).getQuotedPrice(amazonSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "USD"));
    }

    @Test
    public void TorontoExchangeShouldBeSupported() {
        val royalBankOfCanadaSymbol = new StockSymbol("RY");
        val tsxMic = new MarketIdentifierCode("XTSE");
        when(quotes.getQuotedPriceOf(royalBankOfCanadaSymbol, tsxMic)).thenReturn(ONE);

        Money price = stockExchanges.getFor(tsxMic).getQuotedPrice(royalBankOfCanadaSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "CAD"));
    }

    @Test
    public void HongKongStockExchangeShouldBeSupported() {
        val alibabaSymbol = new StockSymbol("9988");
        val hkexMic = new MarketIdentifierCode("XHKG");
        when(quotes.getQuotedPriceOf(alibabaSymbol, hkexMic)).thenReturn(ONE);

        Money price = stockExchanges.getFor(hkexMic).getQuotedPrice(alibabaSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "HKD"));
    }

    @Test
    public void AustralianSecuritiesExchangeShouldBeSupported() {
        val telsraSymbol = new StockSymbol("TLS");
        val asxMic = new MarketIdentifierCode("XASX");
        when(quotes.getQuotedPriceOf(telsraSymbol, asxMic)).thenReturn(ONE);

        Money price = stockExchanges.getFor(asxMic).getQuotedPrice(telsraSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "AUD"));
    }

    @Test
    public void NasdaqShouldBeSupported() {
        val appleSymbol = new StockSymbol("AAPL");
        val nasdaqMic = new MarketIdentifierCode("XNAS");
        when(quotes.getQuotedPriceOf(appleSymbol, nasdaqMic)).thenReturn(ONE);

        Money price = stockExchanges.getFor(nasdaqMic).getQuotedPrice(appleSymbol);

        assertThat(price).isEqualTo(Money.of(ONE, "USD"));
    }

    @Test
    public void shouldThrowForNotSupportedStockExchanges() {
        val madridMic = new MarketIdentifierCode("XMAD");
        
        assertThatExceptionOfType(StockExchangeNotSupported.class).isThrownBy(() -> stockExchanges.getFor(madridMic))
                .withMessage("Market Identifier Code 'XMAD' is not supported")
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    public void shouldNotSupportNullMarketIdentifierCodes() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> stockExchanges.getFor(null));
    }
}
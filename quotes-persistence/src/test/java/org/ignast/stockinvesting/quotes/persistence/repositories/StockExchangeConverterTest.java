package org.ignast.stockinvesting.quotes.persistence.repositories;

import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.ignast.stockinvesting.testutil.MockitoUtils.mock;
import static org.mockito.Mockito.when;

final class StockExchangeConverterTest {

    private final MarketIdentifierCode nasdaqMic = new MarketIdentifierCode("XNAS");

    private final StockExchange nasdaqStockExchange = mock(StockExchange.class, e ->
            when(e.getMarketIdentifierCode()).thenReturn(nasdaqMic));

    private final StockExchanges stockExchanges = mock(StockExchanges.class, s ->
            when(s.getFor(nasdaqMic)).thenReturn(nasdaqStockExchange));

    @Test
    public void shouldPersistExchangeIdToDb() {
        assertThat(new StockExchangeConverter(stockExchanges).convertToDatabaseColumn(nasdaqStockExchange)).isEqualTo("XNAS");
    }

    @Test
    public void shouldRecreateExchangeFromIdInDb() {
        assertThat(new StockExchangeConverter(stockExchanges).convertToEntityAttribute("XNAS")).isEqualTo(nasdaqStockExchange);
    }

    @Test
    public void shouldNotBeCreatedWithNullStockExchanges() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new StockExchangeConverter(null));
    }
}
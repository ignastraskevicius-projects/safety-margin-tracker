package org.ignast.stockinvesting.quotes;

import lombok.NonNull;

import java.util.AbstractMap;
import java.util.Map;

public class StockExchanges {
    private final Map<MarketIdentifierCode, StockExchange> supportedStockExchanges;
    public StockExchanges(QuotesRepository quotes) {
        supportedStockExchanges = Map.ofEntries(
                newStockExchange(new MarketIdentifierCode("XFRA"), new CurrencyCode("EUR"), quotes),
                newStockExchange(new MarketIdentifierCode("XNYS"), new CurrencyCode("USD"), quotes),
                newStockExchange(new MarketIdentifierCode("XTSE"), new CurrencyCode("CAD"), quotes),
                newStockExchange(new MarketIdentifierCode("XHKG"), new CurrencyCode("HKD"), quotes),
                newStockExchange(new MarketIdentifierCode("XASX"), new CurrencyCode("AUD"), quotes),
                newStockExchange(new MarketIdentifierCode("XNAS"), new CurrencyCode("USD"), quotes)
        );
    }

    private AbstractMap.SimpleEntry<MarketIdentifierCode, StockExchange> newStockExchange(MarketIdentifierCode mic, CurrencyCode currency, QuotesRepository quotes) {
        return new AbstractMap.SimpleEntry<>(mic, new StockExchange(mic, currency, quotes));
    }

    public StockExchange getFor(@NonNull MarketIdentifierCode mic) {
        if (supportedStockExchanges.containsKey(mic)) {
            return supportedStockExchanges.get(mic);
        } else {
            throw new StockExchangeNotSupported(mic);
        }
    }
}

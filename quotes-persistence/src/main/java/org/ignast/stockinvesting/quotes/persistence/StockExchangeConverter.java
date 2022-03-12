package org.ignast.stockinvesting.quotes.persistence;

import lombok.NonNull;
import org.ignast.stockinvesting.quotes.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.StockExchange;
import org.ignast.stockinvesting.quotes.StockExchanges;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StockExchangeConverter implements AttributeConverter<StockExchange, String> {

    private StockExchanges stockExchanges;

    public StockExchangeConverter(@NonNull StockExchanges stockExchanges) {
        this.stockExchanges = stockExchanges;
    }

    @Override
    public String convertToDatabaseColumn(StockExchange stockExchange) {
        return stockExchange.getMarketIdentifierCode().get();
    }

    @Override
    public StockExchange convertToEntityAttribute(String marketIdentifierCode) {
        return stockExchanges.getFor(new MarketIdentifierCode(marketIdentifierCode));
    }
}

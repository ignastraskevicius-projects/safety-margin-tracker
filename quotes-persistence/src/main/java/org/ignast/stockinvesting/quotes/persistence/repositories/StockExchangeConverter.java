package org.ignast.stockinvesting.quotes.persistence.repositories;

import lombok.NonNull;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public final class StockExchangeConverter implements AttributeConverter<StockExchange, String> {

    private final StockExchanges stockExchanges;

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

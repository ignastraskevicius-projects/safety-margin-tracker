package org.ignast.stockinvesting.quotes;

import lombok.NonNull;
import lombok.val;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

public class StockExchange {
    private MarketIdentifierCode marketIdentifierCode;
    private CurrencyCode quoteCurrency;
    private QuotesRepository quotes;

    StockExchange(@NonNull MarketIdentifierCode marketIdentifierCode, @NonNull CurrencyCode quoteCurrency, @NonNull QuotesRepository quotes) {
        this.marketIdentifierCode = marketIdentifierCode;
        this.quoteCurrency = quoteCurrency;
        this.quotes = quotes;
    }

    Money getQuotedPrice(@NonNull StockSymbol symbol) {
        val numericPriceValue = quotes.getQuotedPriceOf(symbol, marketIdentifierCode);
        return Money.of(numericPriceValue, quoteCurrency.get());
    }
}

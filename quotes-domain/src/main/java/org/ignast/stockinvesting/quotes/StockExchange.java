package org.ignast.stockinvesting.quotes;

import lombok.NonNull;
import lombok.val;
import org.javamoney.moneta.Money;

import static java.lang.String.format;

public abstract class StockExchange {
    abstract Money getQuotedPrice(StockSymbol symbol);

    static StockExchange create(MarketIdentifierCode marketIdentifierCode, CurrencyCode quoteCurrency, QuotesRepository quotes) {
        val stockExchange = new RegularStockExchange(marketIdentifierCode, quoteCurrency, quotes);
        if (marketIdentifierCode.get().equals("XLON")) {
            return createLondonStockExchange(stockExchange, quoteCurrency, quotes);

        } else {
            return stockExchange;
        }
    }

    private static StockExchange createLondonStockExchange(RegularStockExchange stockExchange, CurrencyCode currency, QuotesRepository quotes) {
        if (!currency.equals(new CurrencyCode("GBP"))) {
            throw new IllegalArgumentException(format("'%s' currency is not supported in stock exchange identified with market identifier 'XLON'", currency.get()));
        } else {
            return new StockExchangeOperatingInPenceDecorator(stockExchange);
        }
    }
}

class StockExchangeOperatingInPenceDecorator extends StockExchange {

    private RegularStockExchange underlyingStockExchange;

    StockExchangeOperatingInPenceDecorator(RegularStockExchange underlyingStockExchange) {
        this.underlyingStockExchange = underlyingStockExchange;
    }

    Money getQuotedPrice(StockSymbol symbol) {
        return underlyingStockExchange.getQuotedPrice(symbol).divide(100);
    }
}

class RegularStockExchange extends StockExchange {
    private MarketIdentifierCode marketIdentifierCode;
    private CurrencyCode quoteCurrency;
    private QuotesRepository quotes;

    RegularStockExchange(@NonNull MarketIdentifierCode marketIdentifierCode, @NonNull CurrencyCode quoteCurrency, @NonNull QuotesRepository quotes) {
        this.marketIdentifierCode = marketIdentifierCode;
        this.quoteCurrency = quoteCurrency;
        this.quotes = quotes;
    }

    Money getQuotedPrice(@NonNull StockSymbol symbol) {
        val numericPriceValue = quotes.getQuotedPriceOf(symbol, marketIdentifierCode);
        return Money.of(numericPriceValue, quoteCurrency.get());
    }
}
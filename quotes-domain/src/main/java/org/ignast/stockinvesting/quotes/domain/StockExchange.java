package org.ignast.stockinvesting.quotes.domain;

import static java.lang.String.format;

import lombok.NonNull;
import lombok.val;
import org.javamoney.moneta.Money;

public final class StockExchange {

    private static final int PENCE_IN_POUND = 100;

    private final MarketIdentifierCode marketIdentifierCode;

    private final CurrencyCode quoteCurrency;

    private final QuotesRepository quotes;

    private StockExchange(
        @NonNull final MarketIdentifierCode marketIdentifierCode,
        @NonNull final CurrencyCode quoteCurrency,
        @NonNull final QuotesRepository quotes
    ) {
        this.marketIdentifierCode = marketIdentifierCode;
        this.quoteCurrency = quoteCurrency;
        this.quotes = quotes;
        getStockExchangeSpecificBehaviour().checkRequirements();
    }

    private StockExchangeSpecificBehaviour getStockExchangeSpecificBehaviour() {
        if (marketIdentifierCode.get().equals("XLON")) {
            return new LondonStockExchangeBehaviour();
        } else {
            return new StandardBehaviour();
        }
    }

    static StockExchange create(
        final MarketIdentifierCode marketIdentifierCode,
        final CurrencyCode quoteCurrency,
        final QuotesRepository quotes
    ) {
        return new StockExchange(marketIdentifierCode, quoteCurrency, quotes);
    }

    public Money getQuotedPrice(@NonNull final StockSymbol symbol) {
        final val numericPriceValue = quotes.getQuotedPriceOf(symbol, marketIdentifierCode);
        final val price = Money.of(numericPriceValue, quoteCurrency.get());
        return getStockExchangeSpecificBehaviour().transformPrice(price);
    }

    public MarketIdentifierCode getMarketIdentifierCode() {
        return marketIdentifierCode;
    }

    private static interface StockExchangeSpecificBehaviour {
        public void checkRequirements();

        public Money transformPrice(Money price);
    }

    private class LondonStockExchangeBehaviour implements StockExchangeSpecificBehaviour {

        @Override
        public void checkRequirements() {
            if (!quoteCurrency.equals(new CurrencyCode("GBP"))) {
                throw new IllegalArgumentException(
                    format(
                        "'%s' currency is not supported in stock exchange identified with market identifier 'XLON'",
                        quoteCurrency.get()
                    )
                );
            }
        }

        @Override
        public Money transformPrice(final Money priceInPounds) {
            return priceInPounds.divide(PENCE_IN_POUND);
        }
    }

    private static final class StandardBehaviour implements StockExchangeSpecificBehaviour {

        @Override
        public void checkRequirements() {}

        @Override
        public Money transformPrice(final Money price) {
            return price;
        }
    }
}

package org.ignast.stockinvesting.quotes.domain;

import lombok.NonNull;
import lombok.val;
import org.javamoney.moneta.Money;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Transient;

import static java.lang.String.format;

@Embeddable
public class StockExchange {
    @Embedded
    private MarketIdentifierCode marketIdentifierCode;

    @Transient
    private CurrencyCode quoteCurrency;

    @Transient
    private QuotesRepository quotes;

    protected StockExchange() {
        //JPA requirement entities to have default constructor
    }

    private StockExchange(@NonNull final MarketIdentifierCode marketIdentifierCode, @NonNull final CurrencyCode quoteCurrency, @NonNull final QuotesRepository quotes) {
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

    static StockExchange create(final MarketIdentifierCode marketIdentifierCode, final  CurrencyCode quoteCurrency, final  QuotesRepository quotes) {
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
        void checkRequirements();

        Money transformPrice(Money price);
    }

    private class LondonStockExchangeBehaviour implements StockExchangeSpecificBehaviour {

        @Override
        public void checkRequirements() {
            if (!quoteCurrency.equals(new CurrencyCode("GBP"))) {
                throw new IllegalArgumentException(format("'%s' currency is not supported in stock exchange identified with market identifier 'XLON'", quoteCurrency.get()));
            }
        }

        @Override
        public Money transformPrice(final Money price) {
            return price.divide(100);
        }
    }

    private static final class StandardBehaviour implements StockExchangeSpecificBehaviour {

        @Override
        public void checkRequirements() {

        }

        @Override
        public Money transformPrice(final Money price) {
            return price;
        }
    }
}


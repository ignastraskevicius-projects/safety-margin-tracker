package org.ignast.stockinvesting.quotes.domain;

import java.math.BigDecimal;

public interface QuotesRepository {

    BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic);

    class QuoteRetrievalFailedException extends RuntimeException {
        public QuoteRetrievalFailedException(final String message, final Exception e) {
            super(message, e);
        }

        public QuoteRetrievalFailedException(final String message) {
            super(message);
        }
    }

}

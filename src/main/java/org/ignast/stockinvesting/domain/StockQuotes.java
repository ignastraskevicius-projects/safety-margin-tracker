package org.ignast.stockinvesting.domain;

import java.math.BigDecimal;

public interface StockQuotes {

    BigDecimal getQuotedPriceOf(Ticker ticker, MarketIdentifierCode mic);

    class QuoteRetrievalFailedException extends RuntimeException {
        public QuoteRetrievalFailedException(String message, Exception e) {
            super(message, e);
        }

        public QuoteRetrievalFailedException(String message) {
            super(message);
        }
    }

}

package org.ignast.stockinvesting.quotes;

import java.math.BigDecimal;

public interface QuotesRepository {

    BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic);

    class QuoteRetrievalFailedException extends RuntimeException {
        public QuoteRetrievalFailedException(String message, Exception e) {
            super(message, e);
        }

        public QuoteRetrievalFailedException(String message) {
            super(message);
        }
    }

}

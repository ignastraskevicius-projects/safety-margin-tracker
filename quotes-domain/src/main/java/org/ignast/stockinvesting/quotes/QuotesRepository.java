package org.ignast.stockinvesting.quotes;

import org.springframework.stereotype.Service;

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

@Service
class FakeQuotesRepository implements QuotesRepository {

    @Override
    public BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic) {
        return BigDecimal.ZERO;
    }
}

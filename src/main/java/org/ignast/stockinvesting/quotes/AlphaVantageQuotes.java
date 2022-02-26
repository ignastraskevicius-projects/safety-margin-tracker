package org.ignast.stockinvesting.quotes;

import org.ignast.stockinvesting.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.domain.StockQuotes;
import org.ignast.stockinvesting.domain.Ticker;

import java.math.BigDecimal;

public class AlphaVantageQuotes implements StockQuotes {
    @Override
    public BigDecimal getQuotedPriceOf(Ticker ticker, MarketIdentifierCode mic) {
        return BigDecimal.ONE;
    }
}

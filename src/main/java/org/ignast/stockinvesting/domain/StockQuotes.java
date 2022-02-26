package org.ignast.stockinvesting.domain;

import java.math.BigDecimal;

public interface StockQuotes {

    BigDecimal getQuotedPriceOf(Ticker ticker, MarketIdentifierCode mic);
}

package org.ignast.stockinvesting.quotes.persistence.testutil;

import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.QuotesRepository;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.testutil.MockitoUtils;

import static java.math.BigDecimal.TEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DomainFactoryForTests {
    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("XNYS");
    }

    public static CompanyName anyCompanyName() {
        return new CompanyName("any");
    }

    public static QuotesRepository anyQuotes() {
        return MockitoUtils.mock(QuotesRepository.class, r -> when(r.getQuotedPriceOf(any(), any())).thenReturn(TEN));
    }
}

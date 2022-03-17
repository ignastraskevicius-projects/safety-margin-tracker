package org.ignast.stockinvesting.quotes.persistence.testutil;

import org.ignast.stockinvesting.quotes.domain.QuotesRepository;
import org.ignast.stockinvesting.testutil.MockitoUtils;

import static java.math.BigDecimal.TEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public final class DomainFactoryForTests {
    public static QuotesRepository anyQuotes() {
        return MockitoUtils.mock(QuotesRepository.class, r -> when(r.getQuotedPriceOf(any(), any())).thenReturn(TEN));
    }
}

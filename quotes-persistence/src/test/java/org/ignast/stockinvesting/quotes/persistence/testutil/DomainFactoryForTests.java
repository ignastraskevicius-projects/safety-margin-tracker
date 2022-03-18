package org.ignast.stockinvesting.quotes.persistence.testutil;

import static java.math.BigDecimal.TEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.ignast.stockinvesting.quotes.domain.QuotesRepository;
import org.ignast.stockinvesting.testutil.MockitoUtils;

public final class DomainFactoryForTests {

    private DomainFactoryForTests() {}

    public static QuotesRepository anyQuotes() {
        return MockitoUtils.mock(
            QuotesRepository.class,
            r -> when(r.getQuotedPriceOf(any(), any())).thenReturn(TEN)
        );
    }
}

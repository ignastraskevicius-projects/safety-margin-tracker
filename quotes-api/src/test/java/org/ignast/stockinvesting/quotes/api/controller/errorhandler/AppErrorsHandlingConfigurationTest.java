package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.ObjectAssert;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.From1ParamConstructor;
import org.junit.jupiter.api.Test;

public final class AppErrorsHandlingConfigurationTest {

    @SuppressWarnings("checkstyle:linelength")
    private static final Map<Class<?>, From1ParamConstructor<String>> TYPES_CONSTRUCTABLE_FROM_STRING = new AppErrorsHandlingConfiguration()
        .apiValidationSupportedTypes()
        .getTypesConstructableFromString();

    @SuppressWarnings("checkstyle:linelength")
    private static final Map<Class<?>, From1ParamConstructor<Integer>> TYPES_CONSTRUCTABLE_FROM_INTEGER = new AppErrorsHandlingConfiguration()
        .apiValidationSupportedTypes()
        .getTypesConstructableFromInteger();

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructorsFromString() {
        final val countOfTypesSupported = asList(
            verifyConstructionFromString(MarketIdentifierCode.class, t -> t.construct("XNYS")),
            verifyConstructionFromString(StockSymbol.class, t -> t.construct("AMZN")),
            verifyConstructionFromString(CompanyName.class, t -> t.construct("Amazon"))
        )
            .size();

        assertThat(TYPES_CONSTRUCTABLE_FROM_STRING).hasSize(countOfTypesSupported);
    }

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructorsFromInteger() {
        final val countOfTypesSupported = List
            .of(verifyConstructionFromInt(CompanyExternalId.class, t -> t.construct(7)))
            .size();

        assertThat(TYPES_CONSTRUCTABLE_FROM_INTEGER).hasSize(countOfTypesSupported);
    }

    private ObjectAssert verifyConstructionFromString(
        final Class<?> type,
        final Construct<String> construct
    ) {
        return assertThat(construct.apply(TYPES_CONSTRUCTABLE_FROM_STRING.get(type))).isInstanceOf(type);
    }

    private ObjectAssert verifyConstructionFromInt(final Class<?> type, final Construct<Integer> construct) {
        return assertThat(construct.apply(TYPES_CONSTRUCTABLE_FROM_INTEGER.get(type))).isInstanceOf(type);
    }

    private static interface Construct<T> {
        public Object apply(From1ParamConstructor<T> constructor);
    }
}

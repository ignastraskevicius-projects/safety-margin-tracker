package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import lombok.val;
import org.assertj.core.api.ObjectAssert;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.From1ParamConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public final class AppErrorsHandlingConfigurationTest {

    private static final Map<Class<?>, From1ParamConstructor<String>> TYPES_CONSTRUCTABLE_FROM_STRING = new AppErrorsHandlingConfiguration().domainClassConstraintSupportedTypes().getTypesConstructableFromString();

    private static final Map<Class<?>, From1ParamConstructor<Integer>> TYPES_CONSTRUCTABLE_FROM_INTEGER = new AppErrorsHandlingConfiguration().domainClassConstraintSupportedTypes().getTypesConstructableFromInteger();

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructorsFromString() {
        final val countOfTypesSupported = asList(
                checkConstructedTypeFromString(MarketIdentifierCode.class, t -> t.construct("XNYS")),
                checkConstructedTypeFromString(StockSymbol.class, t -> t.construct("AMZN")),
                checkConstructedTypeFromString(CompanyName.class, t -> t.construct("Amazon"))
        ).size();

        assertThat(TYPES_CONSTRUCTABLE_FROM_STRING).hasSize(countOfTypesSupported);
    }

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructorsFromInteger() {
        final val countOfTypesSupported = List.of(
                checkConstructedTypeFromInteger(CompanyExternalId.class, t -> t.construct(7))
        ).size();

        assertThat(TYPES_CONSTRUCTABLE_FROM_INTEGER).hasSize(countOfTypesSupported);
    }

    private ObjectAssert checkConstructedTypeFromString(final Class<?> type, final FromConstructorToObject<String> constructor) {
        return assertThat(constructor.construct(TYPES_CONSTRUCTABLE_FROM_STRING.get(type))).isInstanceOf(type);
    }

    private ObjectAssert checkConstructedTypeFromInteger(final Class<?> type, final FromConstructorToObject<Integer> constructor) {
        return assertThat(constructor.construct(TYPES_CONSTRUCTABLE_FROM_INTEGER.get(type))).isInstanceOf(type);
    }

    private static interface FromConstructorToObject<T> {
        Object construct(From1ParamConstructor<T> constructor);
    }
}
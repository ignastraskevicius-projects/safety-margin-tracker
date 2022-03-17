package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import lombok.val;
import org.assertj.core.api.ObjectAssert;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.From1ParamConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
@Configuration
class AppErrorsHandlingConfigurationTest {

    private Map<Class<?>, From1ParamConstructor<String>> typesBackedByString = new AppErrorsHandlingConfiguration().domainClassConstraintSupportedTypes().getTypesConstructableFromString();

    private Map<Class<?>, From1ParamConstructor<Integer>> typesBackedByInteger = new AppErrorsHandlingConfiguration().domainClassConstraintSupportedTypes().getTypesConstructableFromInteger();

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructorsFromString() {
        val countOfTypesSupported = asList(
                checkConstructedTypeFromString(MarketIdentifierCode.class, t -> t.construct("XNYS")),
                checkConstructedTypeFromString(StockSymbol.class, t -> t.construct("AMZN")),
                checkConstructedTypeFromString(UUID.class, t -> t.construct("0c52907c-80de-48dc-84a7-4e02c3842300")),
                checkConstructedTypeFromString(CompanyName.class, t -> t.construct("Amazon"))
        ).size();

        assertThat(typesBackedByString).hasSize(countOfTypesSupported);
    }

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructorsFromInteger() {
        val countOfTypesSupported = asList(
                checkConstructedTypeFromInteger(CompanyExternalId.class, t -> t.construct(7))
        ).size();

        assertThat(typesBackedByInteger).hasSize(countOfTypesSupported);
    }

    private ObjectAssert checkConstructedTypeFromString(Class<?> type, FromConstructorToObject<String> constructor) {
        return assertThat(constructor.construct(typesBackedByString.get(type))).isInstanceOf(type);
    }

    private ObjectAssert checkConstructedTypeFromInteger(Class<?> type, FromConstructorToObject<Integer> constructor) {
        return assertThat(constructor.construct(typesBackedByInteger.get(type))).isInstanceOf(type);
    }

    private static interface FromConstructorToObject<T> {
        Object construct(From1ParamConstructor<T> constructor);
    }
}
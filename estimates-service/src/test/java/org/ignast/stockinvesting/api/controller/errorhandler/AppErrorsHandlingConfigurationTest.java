package org.ignast.stockinvesting.api.controller.errorhandler;

import lombok.val;
import org.assertj.core.api.ObjectAssert;
import org.ignast.stockinvesting.estimates.domain.CompanyName;
import org.ignast.stockinvesting.estimates.domain.CountryCode;
import org.ignast.stockinvesting.estimates.domain.CurrencyCode;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.From1ParamConstructor;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class AppErrorsHandlingConfigurationTest {

    private Map<Class<?>, From1ParamConstructor<String>> types = new AppErrorsHandlingConfiguration().domainClassConstraintSupportedTypes().getTypesConstructableFromString();


    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructors() {
        val countOfTypesSupported = asList(
                checkConstructedType(MarketIdentifierCode.class, t -> t.construct("XNYS")),
                checkConstructedType(StockSymbol.class, t -> t.construct("AMZN")),
                checkConstructedType(CountryCode.class, t -> t.construct("US")),
                checkConstructedType(CurrencyCode.class, t -> t.construct("USD")),
                checkConstructedType(UUID.class, t -> t.construct("0c52907c-80de-48dc-84a7-4e02c3842300")),
                checkConstructedType(CompanyName.class, t -> t.construct("Microsoft"))
        ).size();

        assertThat(types).hasSize(countOfTypesSupported);
    }

    private ObjectAssert checkConstructedType(Class<?> type, FromConstructorToObject constructor) {
        return assertThat(constructor.construct(types.get(type))).isInstanceOf(type);
    }

    private static interface FromConstructorToObject {
        Object construct(From1ParamConstructor constructor);
    }
}
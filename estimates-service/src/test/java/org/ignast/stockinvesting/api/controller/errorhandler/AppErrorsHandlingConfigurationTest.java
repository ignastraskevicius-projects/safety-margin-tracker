package org.ignast.stockinvesting.api.controller.errorhandler;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.ObjectAssert;
import org.ignast.stockinvesting.estimates.domain.CompanyName;
import org.ignast.stockinvesting.estimates.domain.CountryCode;
import org.ignast.stockinvesting.estimates.domain.CurrencyCode;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.From1ParamConstructor;
import org.junit.jupiter.api.Test;

final class AppErrorsHandlingConfigurationTest {

    private final Map<Class<?>, From1ParamConstructor<String>> types = new AppErrorsHandlingConfiguration()
        .apiValidationSupportedTypes()
        .getTypesConstructableFromString();

    @Test
    public void shouldHaveClassCorrectlyAssignedForTypeConstructors() {
        final val countOfTypesSupported = asList(
            checkConstructedType(CountryCode.class, t -> t.construct("US")),
            checkConstructedType(CurrencyCode.class, t -> t.construct("USD")),
            checkConstructedType(UUID.class, t -> t.construct("0c52907c-80de-48dc-84a7-4e02c3842300")),
            checkConstructedType(CompanyName.class, t -> t.construct("Microsoft"))
        )
            .size();

        assertThat(types).hasSize(countOfTypesSupported);
    }

    private ObjectAssert checkConstructedType(
        final Class<?> type,
        final FromConstructorToObject constructor
    ) {
        return assertThat(constructor.construct(types.get(type))).isInstanceOf(type);
    }

    private static interface FromConstructorToObject {
        public Object construct(From1ParamConstructor constructor);
    }
}

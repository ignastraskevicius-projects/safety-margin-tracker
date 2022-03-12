package org.ignast.stockinvesting.util.errorhandling.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ViolationTypeTest {
    @Test
    public void shouldPreserveErrorNames() {
        assertThat(ViolationType.VALUE_MUST_BE_ARRAY.getCorrespondigErrorName()).isEqualTo("valueMustBeArray");
        assertThat(ViolationType.VALUE_INVALID.getCorrespondigErrorName()).isEqualTo("valueIsInvalid");
        assertThat(ViolationType.VALUE_MUST_BE_STRING.getCorrespondigErrorName()).isEqualTo("valueMustBeString");
        assertThat(ViolationType.VALUE_MUST_BE_INTEGER.getCorrespondigErrorName()).isEqualTo("valueMustBeInteger");
        assertThat(ViolationType.FIELD_IS_MISSING.getCorrespondigErrorName()).isEqualTo("fieldIsMissing");
        assertThat(ViolationType.VALUE_MUST_BE_OBJECT.getCorrespondigErrorName()).isEqualTo("valueMustBeObject");
    }

    @Test
    public void invalidValueShouldNotBeSelfExplanatory() {
        assertThat(ViolationType.VALUE_INVALID.isErrorSelfExplanatory()).isEqualTo(false);
    }

    @Test
    public void missingFieldErrorShouldBeSelfExplanatory() {
        assertThat(ViolationType.FIELD_IS_MISSING.isErrorSelfExplanatory()).isEqualTo(true);
    }

    @Test
    public void wrongTypeErrorsShouldBeSelfExplanatory() {
        assertThat(ViolationType.VALUE_MUST_BE_INTEGER.isErrorSelfExplanatory()).isEqualTo(true);
        assertThat(ViolationType.VALUE_MUST_BE_STRING.isErrorSelfExplanatory()).isEqualTo(true);
        assertThat(ViolationType.VALUE_MUST_BE_ARRAY.isErrorSelfExplanatory()).isEqualTo(true);
        assertThat(ViolationType.VALUE_MUST_BE_OBJECT.isErrorSelfExplanatory()).isEqualTo(true);
    }
}
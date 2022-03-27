package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class ViolationTypeTest {

    @Test
    public void shouldPreserveErrorNames() {
        assertThat(ViolationType.VALUE_MUST_BE_ARRAY.getCorrespondingErrorName())
            .isEqualTo("valueMustBeArray");
        assertThat(ViolationType.VALUE_MUST_BE_STRING.getCorrespondingErrorName())
            .isEqualTo("valueMustBeString");
        assertThat(ViolationType.VALUE_MUST_BE_INTEGER.getCorrespondingErrorName())
            .isEqualTo("valueMustBeInteger");
        assertThat(ViolationType.VALUE_MUST_BE_OBJECT.getCorrespondingErrorName())
            .isEqualTo("valueMustBeObject");
    }

    @Test
    public void wrongTypeErrorsShouldBeSelfExplanatory() {
        assertThat(ViolationType.VALUE_MUST_BE_INTEGER.isErrorSelfExplanatory()).isEqualTo(true);
        assertThat(ViolationType.VALUE_MUST_BE_STRING.isErrorSelfExplanatory()).isEqualTo(true);
        assertThat(ViolationType.VALUE_MUST_BE_ARRAY.isErrorSelfExplanatory()).isEqualTo(true);
        assertThat(ViolationType.VALUE_MUST_BE_OBJECT.isErrorSelfExplanatory()).isEqualTo(true);
    }
}

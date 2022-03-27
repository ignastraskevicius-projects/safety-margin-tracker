package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class ViolationTypeTest {

    @Test
    public void shouldPreserveErrorNames() {
        assertThat(ViolationType.VALUE_INVALID.getCorrespondingErrorName()).isEqualTo("valueIsInvalid");
        assertThat(ViolationType.FIELD_IS_MISSING.getCorrespondingErrorName()).isEqualTo("fieldIsMissing");
    }

    @Test
    public void invalidValueShouldNotBeSelfExplanatory() {
        assertThat(ViolationType.VALUE_INVALID.isErrorSelfExplanatory()).isEqualTo(false);
    }

    @Test
    public void missingFieldErrorShouldBeSelfExplanatory() {
        assertThat(ViolationType.FIELD_IS_MISSING.isErrorSelfExplanatory()).isEqualTo(true);
    }
}

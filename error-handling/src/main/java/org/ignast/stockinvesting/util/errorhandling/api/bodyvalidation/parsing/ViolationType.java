package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

enum ViolationType {
    VALUE_MUST_BE_STRING("valueMustBeString", true),
    VALUE_MUST_BE_ARRAY("valueMustBeArray", true),
    VALUE_MUST_BE_OBJECT("valueMustBeObject", true),
    VALUE_MUST_BE_INTEGER("valueMustBeInteger", true);

    private final String correspondingErrorName;

    private final boolean isErrorSelfExplanatory;

    ViolationType(final String correspondingErrorName, final boolean isErrorSelfExplanatory) {
        this.correspondingErrorName = correspondingErrorName;
        this.isErrorSelfExplanatory = isErrorSelfExplanatory;
    }

    public String getCorrespondingErrorName() {
        return correspondingErrorName;
    }

    public boolean isErrorSelfExplanatory() {
        return isErrorSelfExplanatory;
    }
}

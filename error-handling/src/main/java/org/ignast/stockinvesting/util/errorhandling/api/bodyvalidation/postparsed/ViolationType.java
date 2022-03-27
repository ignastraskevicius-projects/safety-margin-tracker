package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

enum ViolationType {
    VALUE_INVALID("valueIsInvalid", false),
    FIELD_IS_MISSING("fieldIsMissing", true);

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

package org.ignast.stockinvesting.api.controller.errorhandler;

public enum ViolationType {
    VALUE_INVALID("fieldHasInvalidValue", false), VALUE_MUST_BE_STRING("valueMustBeString", true),
    VALUE_MUST_BE_ARRAY("valueMustBeArray", true), FIELD_IS_MISSING("fieldIsMissing", true),
    VALUE_MUST_BE_OBJECT("valueMustBeObject", true);

    private String correspondingErrorName;
    private boolean isErrorSelfExplanatory;

    ViolationType(String correspondingErrorName, boolean isErrorSelfExplanatory) {
        this.correspondingErrorName = correspondingErrorName;
        this.isErrorSelfExplanatory = isErrorSelfExplanatory;
    }

    public String getCorrespondigErrorName() {
        return correspondingErrorName;
    }

    public boolean isErrorSelfExplanatory() {
        return isErrorSelfExplanatory;
    }
}

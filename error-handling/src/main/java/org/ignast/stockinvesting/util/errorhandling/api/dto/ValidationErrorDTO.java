package org.ignast.stockinvesting.util.errorhandling.api.dto;

public interface ValidationErrorDTO {
    public String getJsonPath();

    public String getMessage();

    public String getErrorName();
}

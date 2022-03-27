package org.ignast.stockinvesting.util.errorhandling.api.dto;

import org.springframework.http.HttpStatus;

public interface BusinessErrorDTO {
    public String getErrorName();

    public HttpStatus getHttpStatus();
}

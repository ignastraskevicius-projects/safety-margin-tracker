package org.ignast.stockinvesting.util.errorhandling.api;

import org.springframework.http.HttpStatus;

public interface BusinessErrorDTO {
    public String getErrorName();

    public HttpStatus getHttpStatus();
}

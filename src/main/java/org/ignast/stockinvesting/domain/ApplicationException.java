package org.ignast.stockinvesting.domain;

public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }
}

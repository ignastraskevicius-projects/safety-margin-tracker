package org.ignast.stockinvesting.quotes.domain;

public class ApplicationException extends RuntimeException {

    ApplicationException(final String message) {
        super(message);
    }
}

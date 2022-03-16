package org.ignast.stockinvesting.quotes.domain;

public class ApplicationException extends RuntimeException {
    ApplicationException(String message) {
        super(message);
    }
}

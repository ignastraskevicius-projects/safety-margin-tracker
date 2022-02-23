package org.ignast.stockinvesting.api.controller.errorhandler;

public class JacksonParsingErrorExtractionException extends RuntimeException {
    public JacksonParsingErrorExtractionException(String message) {
        super(message);
    }
}

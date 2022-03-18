package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class StrictDeserializingException extends MismatchedInputException {

    protected StrictDeserializingException(final String message, final JsonParser parser) {
        super(parser, message, parser.getTokenLocation());
    }
}

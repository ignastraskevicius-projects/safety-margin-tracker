package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class StrictDeserializingException extends MismatchedInputException {
    protected StrictDeserializingException(JsonParser parser) {
        super(parser, "java.String can only be deserialized only from json String", parser.getTokenLocation());
    }
}

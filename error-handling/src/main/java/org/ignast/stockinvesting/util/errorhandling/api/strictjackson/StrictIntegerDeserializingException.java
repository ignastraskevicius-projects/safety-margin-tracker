package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;

import com.fasterxml.jackson.core.JsonParser;

public class StrictIntegerDeserializingException extends StrictDeserializingException {

    protected StrictIntegerDeserializingException(JsonParser parser) {
        super("java.util.Integer can be deserialized only from json Integer type", parser);
    }
}

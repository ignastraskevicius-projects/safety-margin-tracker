package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;

import com.fasterxml.jackson.core.JsonParser;

public class StrictStringDeserializingException extends StrictDeserializingException {

    protected StrictStringDeserializingException(JsonParser parser) {
        super("java.String can only be deserialized only from json String", parser);
    }
}

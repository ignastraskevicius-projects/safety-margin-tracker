package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.core.JsonParser;

public class StrictStringDeserializingException extends StrictDeserializingException {

    protected StrictStringDeserializingException(JsonParser parser) {
        super(parser);
    }
}

package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;

import com.fasterxml.jackson.core.JsonParser;

public final class StrictIntegerDeserializingException extends StrictDeserializingException {

    StrictIntegerDeserializingException(final JsonParser parser) {
        super("java.util.Integer can be deserialized only from json Integer type", parser);
    }
}

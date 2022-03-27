package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson;

import com.fasterxml.jackson.core.JsonParser;

public final class StrictStringDeserializingException extends StrictDeserializingException {

    StrictStringDeserializingException(final JsonParser parser) {
        super("java.String can only be deserialized only from json String", parser);
    }
}

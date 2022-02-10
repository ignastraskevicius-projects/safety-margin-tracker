package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class StrictStringDeserializer extends StringDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_TRUE)) {
            throw new StrictStringDeserializingException();
        } else if (p.hasToken(JsonToken.VALUE_FALSE)) {
            throw new StrictStringDeserializingException();
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            throw new StrictStringDeserializingException();
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            throw new StrictStringDeserializingException();
        } else if (p.hasToken(JsonToken.VALUE_NULL)) {
            throw new StrictStringDeserializingException();
        } else {
            return p.getText();
        }
    }

    @Override
    public String getNullValue(DeserializationContext ctxt) {
        throw new StrictStringDeserializingException();
    }
}

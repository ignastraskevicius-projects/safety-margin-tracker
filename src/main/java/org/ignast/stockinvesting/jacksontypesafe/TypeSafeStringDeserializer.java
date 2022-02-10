package org.ignast.stockinvesting.jacksontypesafe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class TypeSafeStringDeserializer extends StringDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_TRUE)) {
            throw new StrictStringParsingException();
        } else if (p.hasToken(JsonToken.VALUE_FALSE)) {
            throw new StrictStringParsingException();
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            throw new StrictStringParsingException();
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            throw new StrictStringParsingException();
        } else if (p.hasToken(JsonToken.VALUE_NULL)) {
            throw new StrictStringParsingException();
        } else {
            return p.getText();
        }
    }

    @Override
    public String getNullValue(DeserializationContext ctxt) {
        throw new StrictStringParsingException();
    }
}

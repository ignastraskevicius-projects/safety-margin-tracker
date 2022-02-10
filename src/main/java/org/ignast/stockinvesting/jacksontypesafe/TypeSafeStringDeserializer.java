package org.ignast.stockinvesting.jacksontypesafe;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

public class TypeSafeStringDeserializer extends StringDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            throw MismatchedInputException.from(p, String.class, "");
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            throw MismatchedInputException.from(p, String.class, "");
        } else {
            return p.getText();
        }
    }
}

package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class StrictStringDeserializer extends StringDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        } else {
            throw new StrictStringDeserializingException(p);
        }
    }

    @Override
    public String getNullValue(DeserializationContext ctxt) throws StrictStringDeserializingException {
        throw new StrictStringDeserializingException(ctxt.getParser());
    }
}

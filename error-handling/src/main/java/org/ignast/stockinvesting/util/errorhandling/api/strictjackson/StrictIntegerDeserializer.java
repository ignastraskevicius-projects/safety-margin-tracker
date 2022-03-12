package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class StrictIntegerDeserializer extends StdScalarDeserializer<Integer> {

    public StrictIntegerDeserializer() {
        super(Integer.class);
    }

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getIntValue();
        } else {
            throw new StrictIntegerDeserializingException(p);
        }
    }
}

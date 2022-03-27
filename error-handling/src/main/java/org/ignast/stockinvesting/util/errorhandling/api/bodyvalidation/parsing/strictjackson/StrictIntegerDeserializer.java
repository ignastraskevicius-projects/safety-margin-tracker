package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public final class StrictIntegerDeserializer extends StdScalarDeserializer<Integer> {

    public StrictIntegerDeserializer() {
        super(Integer.class);
    }

    @Override
    public Integer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getIntValue();
        } else {
            throw new StrictIntegerDeserializingException(p);
        }
    }
}

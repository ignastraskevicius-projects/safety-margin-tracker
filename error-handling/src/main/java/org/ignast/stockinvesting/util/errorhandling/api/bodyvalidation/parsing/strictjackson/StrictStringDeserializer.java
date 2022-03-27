package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public final class StrictStringDeserializer extends StringDeserializer {

    @Override
    public String deserialize(final JsonParser p, final DeserializationContext context) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        } else {
            throw new StrictStringDeserializingException(p);
        }
    }
}

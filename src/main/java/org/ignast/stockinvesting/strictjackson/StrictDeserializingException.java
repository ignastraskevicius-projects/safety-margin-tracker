package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

public class StrictDeserializingException extends MismatchedInputException {
    protected StrictDeserializingException(JsonParser parser) {
        super(new TreeTraversingParser(NullNode.getInstance()), "", parser.getTokenLocation());
    }
}

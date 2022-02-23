package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.strictjackson.StrictStringDeserializingException;

import java.util.List;

public class JacksonParsingErrorsExtractor {
    public ValidationErrorDTO extractError(MismatchedInputException exception) {
        if (exception instanceof StrictStringDeserializingException) {
            return new ValidationErrorDTO("", "", ViolationType.VALUE_MUST_BE_STRING);
        } else if (exception.getTargetType() == null) {
            throw new JacksonParsingErrorExtractionException("Jackson parsing failed with no target type defined");
        } else if (List.class.isAssignableFrom(exception.getTargetType())) {
            return new ValidationErrorDTO("", "", ViolationType.VALUE_MUST_BE_ARRAY);
        } else if (exception.getTargetType().getName().endsWith("DTO")) {
            return new ValidationErrorDTO("", "", ViolationType.VALUE_MUST_BE_OBJECT);
        } else {
            throw new JacksonParsingErrorExtractionException("Jackson parsing failed on unexpected target type");
        }
    }
}

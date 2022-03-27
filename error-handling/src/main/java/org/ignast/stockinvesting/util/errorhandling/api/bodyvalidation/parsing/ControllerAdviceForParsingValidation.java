package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ignast.stockinvesting.util.errorhandling.api.dto.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdviceForParsingValidation {

    @NonNull
    private final JacksonParsingErrorsExtractor jacksonParsingErrorsExtractor;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleUnparsableJson(final HttpMessageNotReadableException error) {
        if (error.getCause() instanceof MismatchedInputException) {
            try {
                return StandardErrorDTO.createForBodyDoesNotMatchSchema(
                    List.of(
                        jacksonParsingErrorsExtractor.extractError(
                            (MismatchedInputException) error.getCause()
                        )
                    )
                );
            } catch (JacksonParsingErrorsExtractor.ExtractionException e) {
                return StandardErrorDTO.createNameless(HttpStatus.BAD_REQUEST);
            }
        } else {
            return StandardErrorDTO.createBodyNotParsable();
        }
    }
}

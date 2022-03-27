package org.ignast.stockinvesting.util.errorhandling.api.genericvalidation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ignast.stockinvesting.util.errorhandling.api.dto.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public final class ControllerAdviceForGenericErrors {

    @NonNull
    private final MediaType appMediaType;

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMethodNotAllowed(final HttpRequestMethodNotSupportedException e) {
        return StandardErrorDTO.createForMethodNotAllowed();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleMediaTypeNotAcceptable(final HttpMediaTypeNotAcceptableException e) {
        return StandardErrorDTO.createForNotAcceptableRequiresInstead(appMediaType);
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleUnsupportedContentType(final HttpMediaTypeNotSupportedException e) {
        return StandardErrorDTO.createForUnsupportedContentType();
    }
}

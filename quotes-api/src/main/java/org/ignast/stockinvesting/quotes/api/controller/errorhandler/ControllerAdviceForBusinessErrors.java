package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.quotes.CompanyNotFound;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerAdviceForBusinessErrors {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleCompanyNotFound(CompanyNotFound e) {
        return StandardErrorDTO.createForBusinessError(AppBusinessErrorDTO.createForCompanyNotFound());
    }
}

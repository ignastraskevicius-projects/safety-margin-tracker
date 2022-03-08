package org.ignast.stockinvesting.api.controller.errorhandler;

import org.ignast.stockinvesting.estimates.domain.StockSymbolNotSupported;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerAdviceForBusinessErrors {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleStockSymbolNotSupported(StockSymbolNotSupported e) {
        return StandardErrorDTO.createForBusinessError(AppBusinessErrorDTO.createForStockSymbolNotSupported());
    }


}

package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppBusinessErrorDTO.createForCompanyAlreadyExists;
import static org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppBusinessErrorDTO.createForCompanyNotFound;
import static org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppBusinessErrorDTO.createForListingAlreadyExists;
import static org.ignast.stockinvesting.quotes.api.controller.errorhandler.AppBusinessErrorDTO.createForStockSymbolNotSupportedInThisMarket;
import static org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO.createForBusinessError;

import org.ignast.stockinvesting.quotes.domain.CompanyNotFound;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.CompanyAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.ListingAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public final class ControllerAdviceForBusinessErrors {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleCompanyNotFound(final CompanyNotFound e) {
        return createForBusinessError(createForCompanyNotFound());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleSymbolNotSupportedInMarket(final StockSymbolNotSupportedInThisMarket e) {
        return createForBusinessError(createForStockSymbolNotSupportedInThisMarket());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleCompanyAlreadyExists(final CompanyAlreadyExists e) {
        return createForBusinessError(createForCompanyAlreadyExists());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public StandardErrorDTO handleListingAlreadyExists(final ListingAlreadyExists e) {
        return createForBusinessError(createForListingAlreadyExists());
    }
}

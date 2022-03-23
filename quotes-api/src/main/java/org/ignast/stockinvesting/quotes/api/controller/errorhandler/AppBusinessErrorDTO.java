package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.util.errorhandling.api.BusinessErrorDTO;
import org.springframework.http.HttpStatus;

public final class AppBusinessErrorDTO implements BusinessErrorDTO {

    private final String errorName;

    private final HttpStatus httpStatus;

    private AppBusinessErrorDTO(final String errorName, final HttpStatus httpStatus) {
        this.errorName = errorName;
        this.httpStatus = httpStatus;
    }

    static AppBusinessErrorDTO createForCompanyNotFound() {
        return new AppBusinessErrorDTO(null, HttpStatus.NOT_FOUND);
    }

    public static AppBusinessErrorDTO createForStockSymbolNotSupportedInThisMarket() {
        return new AppBusinessErrorDTO("stockSymbolNotSupportedInThisMarket", HttpStatus.BAD_REQUEST);
    }

    public static AppBusinessErrorDTO createForCompanyAlreadyExists() {
        return new AppBusinessErrorDTO("companyAlreadyExists", HttpStatus.BAD_REQUEST);
    }

    public static AppBusinessErrorDTO createForListingAlreadyExists() {
        return new AppBusinessErrorDTO("listingAlreadyExists", HttpStatus.BAD_REQUEST);
    }

    @Override
    public String getErrorName() {
        return errorName;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.ignast.stockinvesting.util.errorhandling.api.dto.BusinessErrorDTO;
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
        return new AppBusinessErrorDTO("stockSymbolNotSupportedInThisMarket", BAD_REQUEST);
    }

    public static AppBusinessErrorDTO createForCompanyAlreadyExists() {
        return new AppBusinessErrorDTO("companyAlreadyExists", BAD_REQUEST);
    }

    public static AppBusinessErrorDTO createForListingAlreadyExists() {
        return new AppBusinessErrorDTO("listingAlreadyExists", BAD_REQUEST);
    }

    public static AppBusinessErrorDTO createForMarketNotSupported() {
        return new AppBusinessErrorDTO("marketNotSupported", BAD_REQUEST);
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

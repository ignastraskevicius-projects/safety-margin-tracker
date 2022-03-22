package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.util.errorhandling.api.BusinessErrorDTO;

public final class AppBusinessErrorDTO implements BusinessErrorDTO {

    private final String errorName;

    private AppBusinessErrorDTO(final String errorName) {
        this.errorName = errorName;
    }

    static AppBusinessErrorDTO createForCompanyNotFound() {
        return new AppBusinessErrorDTO(null);
    }

    public static AppBusinessErrorDTO createForStockSymbolNotSupportedInThisMarket() {
        return new AppBusinessErrorDTO("stockSymbolNotSupportedInThisMarket");
    }

    public static AppBusinessErrorDTO createForCompanyAlreadyExists() {
        return new AppBusinessErrorDTO("companyAlreadyExists");
    }

    public static AppBusinessErrorDTO createForListingAlreadyExists() {
        return new AppBusinessErrorDTO("listingAlreadyExists");
    }

    @Override
    public String getErrorName() {
        return errorName;
    }
}

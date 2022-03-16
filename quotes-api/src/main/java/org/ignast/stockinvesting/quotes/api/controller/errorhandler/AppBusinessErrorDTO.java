package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.util.errorhandling.api.BusinessErrorDTO;

public class AppBusinessErrorDTO implements BusinessErrorDTO {

    private String errorName;

    private AppBusinessErrorDTO(String errorName) {
        this.errorName = errorName;
    }

    static AppBusinessErrorDTO createForCompanyNotFound() {
        return new AppBusinessErrorDTO(null);
    }

    @Override
    public String getErrorName() {
        return errorName;
    }
}

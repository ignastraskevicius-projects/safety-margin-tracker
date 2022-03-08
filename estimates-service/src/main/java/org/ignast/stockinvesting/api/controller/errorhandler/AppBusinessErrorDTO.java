package org.ignast.stockinvesting.api.controller.errorhandler;

import org.ignast.stockinvesting.util.errorhandling.api.BusinessErrorDTO;

public class AppBusinessErrorDTO implements BusinessErrorDTO {

    private String errorName;

    private AppBusinessErrorDTO(String errorName) {
        this.errorName = errorName;
    }

    static AppBusinessErrorDTO createForStockSymbolNotSupported() {
        return new AppBusinessErrorDTO("stockSymbolNotSupported");
    }

    @Override
    public String getErrorName() {
        return errorName;
    }
}

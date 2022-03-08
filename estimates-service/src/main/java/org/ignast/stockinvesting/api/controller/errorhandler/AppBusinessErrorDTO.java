package org.ignast.stockinvesting.api.controller.errorhandler;

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

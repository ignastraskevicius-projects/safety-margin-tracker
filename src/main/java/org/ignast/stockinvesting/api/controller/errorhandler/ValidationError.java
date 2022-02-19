package org.ignast.stockinvesting.api.controller.errorhandler;

public class ValidationError {
    private String path;

    public ValidationError(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

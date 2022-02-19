package org.ignast.stockinvesting.api.controller.errorhandler;

public class ValidationError {
    private String path;
    private String message;

    public ValidationError(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}

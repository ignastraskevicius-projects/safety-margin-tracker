package org.ignast.stockinvesting.api.controller.errorhandler;

public class ValidationError {
    private String path;
    private String message;
    private ViolationType type;

    public ValidationError(String path, String message, ViolationType type) {
        this.path = path;
        this.message = message;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public ViolationType getType() {
        return type;
    }
}

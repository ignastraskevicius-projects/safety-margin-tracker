package org.ignast.stockinvesting.api.controller;

public class BodySchemaMismatchJsonErrors {
    public static String forMissingFieldAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forStringRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forObjectRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeObject\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forArrayRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldMustBeArray\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }
}

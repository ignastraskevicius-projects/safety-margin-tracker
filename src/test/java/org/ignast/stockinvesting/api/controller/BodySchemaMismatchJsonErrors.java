package org.ignast.stockinvesting.api.controller;

public class BodySchemaMismatchJsonErrors {

    public static String forMissingFieldAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forTwoMissingFieldsAt(String jsonPath1, String jsonPath2) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}, {\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath1, jsonPath2);
    }

    public static String forStringRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forObjectRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeObject\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forArrayRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forInvalidValueAt(String jsonPath, String message) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldHasInvalidValue\",\"jsonPath\":\"%s\",\"message\":\"%s\"}]}",
                jsonPath, message);
    }
}

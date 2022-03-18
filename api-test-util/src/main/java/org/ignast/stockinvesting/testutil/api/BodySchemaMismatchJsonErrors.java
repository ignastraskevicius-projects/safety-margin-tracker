package org.ignast.stockinvesting.testutil.api;

import static java.lang.String.format;

public final class BodySchemaMismatchJsonErrors {

    private BodySchemaMismatchJsonErrors() {}

    public static String forMissingFieldAt(final String jsonPath) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"fieldIsMissing",
                                "jsonPath":"%s"
                            }]
                        }
                """,
            jsonPath
        );
    }

    public static String forTwoMissingFieldsAt(final String jsonPath1, final String jsonPath2) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"fieldIsMissing",
                                "jsonPath":"%s"
                            }, {
                                "errorName":"fieldIsMissing",
                                "jsonPath":"%s"
                            }]
                        }""",
            jsonPath1,
            jsonPath2
        );
    }

    public static String forStringRequiredAt(final String jsonPath) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeString",
                                "jsonPath":"%s"
                            }]
                        }""",
            jsonPath
        );
    }

    public static String forIntegerRequiredAt(final String jsonPath) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeInteger",
                                "jsonPath":"%s"
                            }]
                        }""",
            jsonPath
        );
    }

    public static String forObjectRequiredAt(final String jsonPath) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeObject",
                                "jsonPath":"%s"
                            }]
                        }""",
            jsonPath
        );
    }

    public static String forArrayRequiredAt(final String jsonPath) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeArray",
                                "jsonPath":"%s"
                            }]
                        }""",
            jsonPath
        );
    }

    public static String forInvalidValueAt(final String jsonPath, final String message) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueIsInvalid",
                                "jsonPath":"%s","message":"%s"
                            }]
                        }""",
            jsonPath,
            message
        );
    }

    public static String forInvalidValuesAt(
        final String jsonPath1,
        final String message1,
        final String jsonPath2,
        final String message2
    ) {
        return format(
            """
                        {
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueIsInvalid",
                                "jsonPath":"%s","message":"%s"
                            },{
                                "errorName":"valueIsInvalid",
                                "jsonPath":"%s",
                                "message":"%s"
                            }]}""",
            jsonPath1,
            message1,
            jsonPath2,
            message2
        );
    }
}

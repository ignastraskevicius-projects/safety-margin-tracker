package org.ignast.stockinvesting.testutil.api;

import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forArrayRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forIntegerRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forInvalidValueAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forInvalidValuesAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forObjectRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forStringRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forTwoMissingFieldsAt;
import static org.ignast.stockinvesting.testutil.api.JsonAssert.assertThatJson;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

final class BodySchemaMismatchJsonErrorsTest {

    @Test
    public void shouldCreateErrorJsonForMissingField() throws JSONException {
        assertThatJson(forMissingFieldAt("someJsonPath"))
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"fieldIsMissing",
                                "jsonPath":"someJsonPath"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateErrorJsonForMultipleMissingFields() throws JSONException {
        assertThatJson(forTwoMissingFieldsAt("someJsonPath", "someOtherPath"))
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"fieldIsMissing",
                                "jsonPath":"someJsonPath"
                            }, {
                                "errorName":"fieldIsMissing",
                                "jsonPath":"someOtherPath"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateErrorJsonForStringRequiredField() throws JSONException {
        assertThatJson(forStringRequiredAt("someJsonPath"))
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeString",
                                "jsonPath":"someJsonPath"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateErrorJsonForIntegerRequiredField() throws JSONException {
        assertThatJson(forIntegerRequiredAt("someJsonPath"))
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeInteger",
                                "jsonPath":"someJsonPath"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateErrorJsonForObjectRequiredField() throws JSONException {
        assertThatJson(forObjectRequiredAt("someJsonPath"))
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueMustBeObject",
                                "jsonPath":"someJsonPath"
                            }]
                        }"""
            );
    }

    @Test
    public void shouldCreateErrorJsonForArrayRequiredField() throws JSONException {
        assertThatJson(forArrayRequiredAt("someJsonPath"))
            .isEqualTo(
                """
                    {
                        "httpStatus":400,
                        "errorName":"bodyDoesNotMatchSchema",
                        "validationErrors":[{
                            "errorName":"valueMustBeArray",
                            "jsonPath":"someJsonPath"
                        }]
                    }"""
            );
    }

    @Test
    void shouldCreateErrorJsonForInvalidValue() throws JSONException {
        assertThatJson(forInvalidValueAt("someJsonPath", "someMessage"))
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueIsInvalid",
                                "jsonPath":"someJsonPath",
                                "message":"someMessage"
                            }]
                        }"""
            );
    }

    @Test
    void shouldCreateErrorJsonForMultipleInvalidValues() throws JSONException {
        assertThatJson(
            forInvalidValuesAt("someJsonPath", "someMessage", "someOtherJsonPath", "someOtherMessage")
        )
            .isEqualTo(
                """
                        {
                            "httpStatus":400,
                            "errorName":"bodyDoesNotMatchSchema",
                            "validationErrors":[{
                                "errorName":"valueIsInvalid",
                                "jsonPath":"someJsonPath",
                                "message":"someMessage"
                            },{
                                "errorName":"valueIsInvalid",
                                "jsonPath":"someOtherJsonPath",
                                "message":"someOtherMessage"
                            }]
                        }"""
            );
    }
}

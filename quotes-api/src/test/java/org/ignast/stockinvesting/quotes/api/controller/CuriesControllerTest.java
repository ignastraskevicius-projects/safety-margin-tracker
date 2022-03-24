package org.ignast.stockinvesting.quotes.api.controller;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CuriesControllerTest {

    private static final String APP_V1 = "application/vnd.stockinvesting.quotes-v1.hal+json";

    @Test
    public void shouldIndicateAttributesRequiredForCompanyCreation() {
        assertThat(new CuriesController().getCuriesForCompanyCreation())
            .isEqualTo(format("""
                {"mediaType":"%s","methods":[{"method":"PUT"}]}""", APP_V1));
    }

    @Test
    public void shouldIndicateAttributesRequiredForRetrievingPrice() {
        assertThat(new CuriesController().getCuriesForRetrievingQuotedPrice())
            .isEqualTo(format("""
                {"mediaType":"%s","methods":[{"method":"GET"}]}""", APP_V1));
    }
}

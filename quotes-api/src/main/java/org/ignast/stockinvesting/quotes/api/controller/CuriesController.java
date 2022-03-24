package org.ignast.stockinvesting.quotes.api.controller;

import static java.lang.String.format;
import static org.ignast.stockinvesting.quotes.api.controller.VersionedApiMediaTypes.V1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class CuriesController {

    @GetMapping(value = "/rels/quotes/createCompany", produces = V1)
    public String getCuriesForCompanyCreation() {
        return format("{\"mediaType\":\"%s\",\"methods\":[{\"method\":\"PUT\"}]}", V1);
    }

    @GetMapping(value = "/rels/quotes/queryQuotedPrice", produces = V1)
    public String getCuriesForRetrievingQuotedPrice() {
        return format("{\"mediaType\":\"%s\",\"methods\":[{\"method\":\"GET\"}]}", V1);
    }
}

package org.ignast.stockinvesting.quotes.api.controller;

import lombok.NonNull;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Companies;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies/{id}")
public class PriceController {

    private Companies companies;

    public PriceController(@NonNull final Companies companies) {
        this.companies = companies;
    }

    @GetMapping(value = "/price", produces = VersionedApiMediaTypes.V1)
    public PriceDTO retrievePriceForCompanyWithId(@PathVariable(name = "id") final int companyId) {
        final val price = companies.findByExternalId(new CompanyExternalId(companyId)).getQuotedPrice();
        return new PriceDTO(price.getNumberStripped(), price.getCurrency().getCurrencyCode());
    }
}

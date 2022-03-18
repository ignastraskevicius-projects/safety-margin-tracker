package org.ignast.stockinvesting.api.controller;

import java.util.Currency;
import lombok.val;
import org.ignast.stockinvesting.domain.Companies;
import org.ignast.stockinvesting.estimates.domain.Company;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("companies")
public class CompanyController {

    private final Companies companies;

    public CompanyController(final Companies companies) {
        this.companies = companies;
    }

    @PutMapping(consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@Validated @RequestBody final CompanyDTO company) {
        final val currency = Currency.getInstance(company.getFunctionalCurrency());
        companies.create(new Company(company.getName(), company.getHomeCountry(), currency));

        return new ResponseEntity<>("", HttpStatus.CREATED);
    }
}

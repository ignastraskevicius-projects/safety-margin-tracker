package org.ignast.stockinvesting.api.controller;

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

import java.util.Currency;

@RestController
@RequestMapping("companies")
public class CompanyController {

    private final Companies companies;

    public CompanyController(Companies companies) {
        this.companies = companies;
    }

    @PutMapping(consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@Validated @RequestBody CompanyDTO company) {
        val currency = Currency.getInstance(company.getFunctionalCurrency());
        companies.create(new Company(company.getName(), company.getHomeCountry(), currency));

        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

}

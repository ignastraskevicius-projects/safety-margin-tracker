package org.ignast.stockinvesting.api.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("companies")
public class CompanyController {

    @PostMapping(consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@Validated @RequestBody CompanyDTO company) {
        if (company.getFunctionalCurrency().equals("EUR") || company.getFunctionalCurrency().equals("USD")) {
            return new ResponseEntity<>("", HttpStatus.CREATED);
        } else {
            throw new IllegalArgumentException();
        }
    }

}

package org.ignast.stockinvesting.quotes.api.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.http.HttpResponse;

@RestController
public class CompanyController {

    @PutMapping(value = "/companies", consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> createCompany(@Valid @RequestBody CompanyDTO companyDTO){
        return new ResponseEntity("", HttpStatus.CREATED);
    }
}

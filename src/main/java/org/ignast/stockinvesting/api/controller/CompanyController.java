package org.ignast.stockinvesting.api.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("companies")
public class CompanyController {

    @PostMapping(consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@Valid @RequestBody CompanyDTO company) {
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

}

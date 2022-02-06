package org.ignast.stockinvesting.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("companies")
public class CompanyController {

    @PostMapping
    public HttpEntity<String> defineCompany() {
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

}

package org.ignast.stockinvesting.quotes.api.controller.root;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
public class CompanyController {

    @RequestMapping("/companies")
    public HttpEntity<String> createCompany(){
        return new ResponseEntity("", HttpStatus.CREATED);
    }
}

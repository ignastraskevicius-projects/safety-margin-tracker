package org.ignast.stockinvesting.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("companies")
public class CompanyController {

    @PostMapping(consumes = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@RequestBody CompanyDTO company) {
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

}

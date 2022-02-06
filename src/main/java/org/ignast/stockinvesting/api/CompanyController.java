package org.ignast.stockinvesting.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("companies")
public class CompanyController {

    @PostMapping(produces = VersionedApiMediaTypes.V1)
    public HttpEntity<String> defineCompany(@RequestBody String body) {
        return new ResponseEntity<>("", "{}".equals(body) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

}

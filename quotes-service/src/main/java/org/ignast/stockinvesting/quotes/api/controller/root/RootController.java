package org.ignast.stockinvesting.quotes.api.controller.root;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

    @GetMapping(value = "/", produces = VersionedApiMediaTypes.V1)
    public HttpEntity<String> getRoot() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}

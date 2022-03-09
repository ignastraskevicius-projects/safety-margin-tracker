package org.ignast.stockinvesting.quotes.api.controller.root;

import org.ignast.stockinvesting.quotes.api.controller.CompanyController;
import org.ignast.stockinvesting.quotes.api.controller.CompanyDTO;
import org.ignast.stockinvesting.quotes.api.controller.VersionedApiMediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Arrays.asList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

    @GetMapping(value = "/", produces = VersionedApiMediaTypes.V1)
    public HttpEntity<Root> getRoot() {
        Root root = new Root();
        root.add(linkTo(methodOn(CompanyController.class).createCompany(new CompanyDTO("any", "any", asList()))).withRel("quotes:company"));
        return new ResponseEntity<>(root, HttpStatus.OK);
    }
}

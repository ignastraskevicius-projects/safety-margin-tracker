package org.ignast.stockinvesting.quotes.api.controller.root;

import lombok.val;
import org.ignast.stockinvesting.quotes.api.controller.CompanyController;
import org.ignast.stockinvesting.quotes.api.controller.CompanyDTO;
import org.ignast.stockinvesting.quotes.api.controller.VersionedApiMediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public final class RootController {

    @GetMapping(value = "/", produces = VersionedApiMediaTypes.V1)
    public HttpEntity<Root> getRoot() {
        final val root = new Root();
        root.add(linkTo(methodOn(CompanyController.class).createCompany(new CompanyDTO(1, "any", List.of()))).withRel("quotes:company"));
        return new ResponseEntity<>(root, HttpStatus.OK);
    }
}

package org.ignast.stockinvesting.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class RootController {

    @GetMapping(value = "/", produces = VersionedApiMediaTypes.V1)
    public HttpEntity<Root> getRoot() {
        final val root = new Root();
        root.add(
            linkTo(
                methodOn(CompanyController.class)
                    .defineCompany(
                        new CompanyDTO(
                            "someId",
                            "Amazon",
                            "Romania",
                            "United States Dollar",
                            List.of(new ListingDTO("New York Stock Exchange", "Amazon"))
                        )
                    )
            )
                .withRel("stocks:company")
        );
        return new ResponseEntity<>(root, HttpStatus.OK);
    }
}

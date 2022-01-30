package org.ignast.stockinvesting.api;

import org.springframework.hateoas.LinkRelation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

    @RequestMapping("/")
    public HttpEntity<Root> root() {
        Root root = new Root();
        root.add(linkTo(methodOn(RootController.class).root()).withRel("stocks:company"));
        return new ResponseEntity<>(root, HttpStatus.OK);
    }
}
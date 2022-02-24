package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GenericErrorController implements ErrorController {

    @RequestMapping("/error")

    public ResponseEntity<StandardErrorDTO> handleError() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardErrorDTO.createForResourceNotFound());
    }
}

package org.ignast.stockinvesting.api.controller.errorhandler;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GenericErrorController implements ErrorController {

    private final int INTERNAL_SERVER_ERROR = 500;

    @RequestMapping("/error")
    public ResponseEntity<StandardErrorDTO> handleError(HttpServletRequest request) {
        try {
            int statusCode = (int) request.getAttribute("javax.servlet.error.status_code");
            return ResponseEntity.status(statusCode).body(StandardErrorDTO.createNameless());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(StandardErrorDTO.createNameless());
        }
    }
}

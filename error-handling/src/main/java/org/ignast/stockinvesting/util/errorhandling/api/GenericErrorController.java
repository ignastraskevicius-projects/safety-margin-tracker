package org.ignast.stockinvesting.util.errorhandling.api;

import javax.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public final class GenericErrorController implements ErrorController {

    private static final int INTERNAL_SERVER_ERROR = 500;

    @RequestMapping("/error")
    @SuppressWarnings("checkstyle:illegalcatch")
    public ResponseEntity<StandardErrorDTO> handleError(final HttpServletRequest request) {
        try {
            final val statusCode = (int) request.getAttribute("javax.servlet.error.status_code");
            return ResponseEntity
                .status(statusCode)
                .body(StandardErrorDTO.createNameless(HttpStatus.valueOf(statusCode)));
        } catch (Exception e) {
            return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(StandardErrorDTO.createNameless(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}

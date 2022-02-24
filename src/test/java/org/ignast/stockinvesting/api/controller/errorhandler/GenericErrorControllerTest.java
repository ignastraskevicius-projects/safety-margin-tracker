package org.ignast.stockinvesting.api.controller.errorhandler;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class GenericErrorControllerTest {

    private final GenericErrorController controller = new GenericErrorController();

    @Test
    public void shouldHandleNotFoundResources() {
        val response = controller.handleError();
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody().getErrorName()).isEqualTo("resourceNotFound");
    }
}

package org.ignast.stockinvesting.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class GenericWebErrorsFormatterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldIndicateResourceNoReadable() throws Exception {
        mockMvc.perform(get("/").accept(HAL_JSON)).andExpect(status().isMethodNotAllowed())
                .andExpect(content().string("{\"errorName\":\"methodNotAllowed\"}"));
    }

    @Configuration
    static class Controllers {

        @Bean
        public GenericWebErrorsFormatter errorsControllerAdvice() {
            return new GenericWebErrorsFormatter();
        }

        @Bean
        public PostOnlyController postOnlyController() {
            return new PostOnlyController();
        }
    }

    @RestController
    static class PostOnlyController {

        @PostMapping("/")
        public String postOnly() {
            return "success";
        }
    }
}

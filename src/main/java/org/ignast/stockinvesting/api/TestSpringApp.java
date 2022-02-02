package org.ignast.stockinvesting.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.http.MediaType;

@SpringBootApplication
public class TestSpringApp {
    public static void main(String[] args) {
        SpringApplication.run(TestSpringApp.class, args);
    }

    @Bean
    public HalConfiguration halAcceptingServiceSpecificMediaType() {
        return new HalConfiguration().withMediaType(MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json"));
    }
}

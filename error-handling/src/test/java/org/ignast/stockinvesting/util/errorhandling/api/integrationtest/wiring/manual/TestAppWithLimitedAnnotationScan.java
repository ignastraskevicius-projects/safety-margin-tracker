package org.ignast.stockinvesting.util.errorhandling.api.integrationtest.wiring.manual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestAppWithLimitedAnnotationScan {
    public static void main(String[] args) {
        SpringApplication.run(TestAppWithLimitedAnnotationScan.class, args);
    }
}
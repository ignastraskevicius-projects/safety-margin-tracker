package org.ignast.stockinvesting.util.errorhandling.api.integrationtest.wiring.manual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings({"checkstyle:finalclass", "checkstyle:hideutilityclassconstructor"})
@SpringBootApplication
public class TestAppWithLimitedAnnotationScan {
    public static void main(final String[] args) {
        SpringApplication.run(TestAppWithLimitedAnnotationScan.class, args);
    }
}
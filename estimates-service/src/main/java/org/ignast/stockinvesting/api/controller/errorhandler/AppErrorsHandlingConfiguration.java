package org.ignast.stockinvesting.api.controller.errorhandler;

import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.UUID;
import org.ignast.stockinvesting.estimates.domain.CompanyId;
import org.ignast.stockinvesting.estimates.domain.CompanyName;
import org.ignast.stockinvesting.estimates.domain.CountryCode;
import org.ignast.stockinvesting.estimates.domain.CurrencyCode;
import org.ignast.stockinvesting.util.errorhandling.api.ErrorExtractorConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ErrorExtractorConfiguration.class)
public class AppErrorsHandlingConfiguration {

    @Bean
    public DomainClassConstraint.SupportedTypes apiValidationSupportedTypes() {
        return DomainClassConstraint.SupportedTypes.supporting(
            Map.of(
                CountryCode.class,
                CountryCode::new,
                CurrencyCode.class,
                CurrencyCode::new,
                UUID.class,
                CompanyId::toUUID,
                CompanyName.class,
                CompanyName::new
            ),
            emptyMap()
        );
    }
}

package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import org.ignast.stockinvesting.quotes.*;
import org.ignast.stockinvesting.util.errorhandling.api.ErrorExtractorConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.UUID;

@Configuration
@Import(ErrorExtractorConfiguration.class)
public class AppErrorsHandlingConfiguration {

    @Bean
    public DomainClassConstraint.SupportedTypes domainClassConstraintSupportedTypes() {
        return DomainClassConstraint.SupportedTypes.supporting(Map.of(MarketIdentifierCode.class, MarketIdentifierCode::new, StockSymbol.class, StockSymbol::new, UUID.class, CompanyId::toUUID, CompanyName.class, CompanyName::new),
                Map.of(PositiveNumber.class, PositiveNumber::new));
    }

}
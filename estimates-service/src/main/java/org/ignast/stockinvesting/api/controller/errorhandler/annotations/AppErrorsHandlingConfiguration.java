package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import org.ignast.stockinvesting.api.controller.errorhandler.GenericErrorHandlingConfiguration;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration
@Import(GenericErrorHandlingConfiguration.class)
public class AppErrorsHandlingConfiguration {

    @Bean
    public DomainClassConstraintValidator.SupportedTypes supportedTypes() {
        return DomainClassConstraintValidator.SupportedTypes.supporting(Map.of(MarketIdentifierCode.class, MarketIdentifierCode::new, StockSymbol.class, StockSymbol::new));
    }

}
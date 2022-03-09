package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import org.ignast.stockinvesting.estimates.domain.*;
import org.ignast.stockinvesting.util.errorhandling.api.GenericErrorHandlingConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.SupportedTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.UUID;

@Configuration
@Import(GenericErrorHandlingConfiguration.class)
public class AppErrorsHandlingConfiguration {

    @Bean
    public SupportedTypes supportedTypes() {
        return SupportedTypes.supporting(Map.of(MarketIdentifierCode.class, MarketIdentifierCode::new, StockSymbol.class, StockSymbol::new, CountryCode.class, CountryCode::new, CurrencyCode.class, CurrencyCode::new, UUID.class, CompanyId::toUUID));
    }

}
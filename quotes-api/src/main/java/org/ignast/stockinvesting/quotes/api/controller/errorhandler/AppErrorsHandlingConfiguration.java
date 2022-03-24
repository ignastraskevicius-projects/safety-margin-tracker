package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.ignast.stockinvesting.quotes.api.controller.VersionedApiMediaTypes.V1;

import java.util.Map;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.util.errorhandling.api.ErrorExtractorConfiguration;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Configuration
@Import(ErrorExtractorConfiguration.class)
public class AppErrorsHandlingConfiguration {

    @Bean
    public DomainClassConstraint.SupportedTypes apiValidationSupportedTypes() {
        return DomainClassConstraint.SupportedTypes.supporting(
            Map.of(
                MarketIdentifierCode.class,
                MarketIdentifierCode::new,
                StockSymbol.class,
                StockSymbol::new,
                CompanyName.class,
                CompanyName::new
            ),
            Map.of(CompanyExternalId.class, CompanyExternalId::new)
        );
    }

    @Bean
    MediaType appMediaType() {
        return MediaType.parseMediaType(V1);
    }
}

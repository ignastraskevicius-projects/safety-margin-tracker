package org.ignast.stockinvesting.quotes.api.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.http.MediaType;

@Configuration
public class HalConfig {

    @Bean
    public HalConfiguration halAcceptingServiceSpecificMediaType() {
        return new HalConfiguration().withMediaType(MediaType.valueOf(VersionedApiMediaTypes.V1));
    }

}

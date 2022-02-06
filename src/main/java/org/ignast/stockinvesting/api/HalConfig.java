package org.ignast.stockinvesting.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.http.MediaType;

@Configuration
public class HalConfig {

    @Bean
    public HalConfiguration halAcceptingServiceSpecificMediaType() {
        return new HalConfiguration()
                .withMediaType(MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json"));
    }

}

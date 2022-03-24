package org.ignast.stockinvesting.quotes.api.controller;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.http.MediaType;

@Configuration
public class HalConfig {

    @Bean
    public HalConfiguration halAcceptingServiceSpecificMediaType() {
        return new HalConfiguration().withMediaType(MediaType.valueOf(VersionedApiMediaTypes.V1));
    }

    @Bean
    public CurieProvider curieProvider(@Value("${documentation.url}") final String docsUrl) {
        return new DefaultCurieProvider("quotes", UriTemplate.of(format("%s/rels/quotes/{rel}", docsUrl)));
    }
}

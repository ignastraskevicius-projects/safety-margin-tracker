package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

@JsonComponent
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer turnOffScientificNotation() {
        return b -> b.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    }
}

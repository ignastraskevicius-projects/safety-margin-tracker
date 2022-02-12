package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

@JsonComponent
public class MyObjectMapper {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.deserializers(new StrictStringDeserializer())
                .postConfigurer(mapper -> mapper.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES));
    }
}

package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaTypeInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new MediaTypeCheckerInterceptor(objectMapper));
    }
}

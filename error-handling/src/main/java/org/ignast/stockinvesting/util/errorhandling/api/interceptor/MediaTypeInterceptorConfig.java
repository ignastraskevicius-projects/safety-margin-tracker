package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaTypeInterceptorConfig implements WebMvcConfigurer {
    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MediaTypeCheckerInterceptor());
    }
}

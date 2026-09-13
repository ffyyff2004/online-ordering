package com.boot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAccessConfig implements WebMvcConfigurer {
    private final OrderAccess access;
    public WebAccessConfig(OrderAccess access) { this.access = access; }
    @Override
    public void addInterceptors(InterceptorRegistry registry) { registry.addInterceptor(access).addPathPatterns("/**"); }
}


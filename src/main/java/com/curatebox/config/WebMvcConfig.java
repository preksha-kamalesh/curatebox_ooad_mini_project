package com.curatebox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminSessionInterceptor adminSessionInterceptor;

    public WebMvcConfig(AdminSessionInterceptor adminSessionInterceptor) {
        this.adminSessionInterceptor = adminSessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminSessionInterceptor)
                .addPathPatterns(
                        "/dashboard",
                    "/boxes/**",
                        "/customers/**",
                        "/subscriptions/**",
                        "/inventory/**",
                        "/api/customers/**",
                        "/api/subscriptions/**",
                        "/api/products/**",
                        "/api/suppliers/**",
                        "/api/reports/**",
                        "/api/boxes/**",
                        "/api/admin/dashboard"
                )
                .excludePathPatterns(
                        "/admin/login",
                        "/api/admin/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/h2-console/**",
                        "/error"
                );
    }
}
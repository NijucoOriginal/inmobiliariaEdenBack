package com.jsebastian.eden.EdenSys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    /**
     * Clase que permite el manejo de cors globales para todo el proyecto general
     * permite las variables de entorno frontend.url y frontend.local.url
     * Para después , SecurityConfig gestiona el acceso a los endpoints
     */

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${frontend.local.url}")
    private String frontendLocalUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(frontendUrl, frontendLocalUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}

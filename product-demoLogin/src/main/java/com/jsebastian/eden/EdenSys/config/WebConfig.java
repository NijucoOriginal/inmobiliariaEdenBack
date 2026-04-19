package com.jsebastian.eden.EdenSys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${frontend.local.url}")
    private String frontendLocalUrl;

    @Value("${cloudfront.url:}")
    private String cloudfrontUrl;

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        List<String> origins = new ArrayList<>();
        origins.add(frontendUrl);
        origins.add(frontendLocalUrl);
        if (cloudfrontUrl != null && !cloudfrontUrl.isBlank()) {
            origins.add(cloudfrontUrl);
        }

        System.out.println(">>> CORS orígenes permitidos: " + origins); // para verificar en logs

        config.setAllowedOrigins(origins);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;

        public WebMvcConfigurer corsConfigurer () {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/api/**")
                            .allowedOrigins(frontendUrl, frontendLocalUrl, "http://localhost:5678", "http://localhost:8080")
                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                            .allowCredentials(true);
                }
            };
        }



        @Bean
        public CorsFilter corsFilter (UrlBasedCorsConfigurationSource corsConfigurationSource){
            return new CorsFilter(corsConfigurationSource);
        }
    }
}
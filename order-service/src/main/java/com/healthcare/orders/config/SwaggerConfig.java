package com.healthcare.orders.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI orderServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Handles order processing")
                        .version("1.0"));
    }
}
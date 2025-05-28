package com.healthcare.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class AuthLoggingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            private final Logger logger = LoggerFactory.getLogger(AuthLoggingConfig.class);

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                logger.info("Auth request: {} {}", request.getMethod(), request.getRequestURI());
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                        Object handler, Exception ex) {
                logger.info("Auth response: {}", response.getStatus());
            }
        });
    }
}

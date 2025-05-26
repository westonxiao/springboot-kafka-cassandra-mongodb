package com.healthcare.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Component
public class ResponseCacheFilter extends AbstractGatewayFilterFactory<ResponseCacheFilter.Config> {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public ResponseCacheFilter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String cacheKey = exchange.getRequest().getURI().getPath()
                    + "?" + exchange.getRequest().getURI().getQuery();

            return redisTemplate.opsForValue().get(cacheKey)
                    .flatMap(cachedResponse -> {
                        if (cachedResponse != null) {
                            exchange.getResponse().getHeaders().add("X-Cache-Hit", "true");
                            return exchange.getResponse().setComplete(); // Mono<Void>
                        }

                        return chain.filter(exchange).then(Mono.defer(() -> {
                            if (exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                                return redisTemplate.opsForValue()
                                        .set(cacheKey, "some-value", Duration.ofSeconds(config.ttl))
                                        .then(); // <<--- this converts Mono<Boolean> to Mono<Void>
                            }
                            return Mono.empty(); // Mono<Void>
                        }));
                    });
        };
    }

    public static class Config {
        private int ttl = 30; // Cache TTL in seconds
        // Getters and setters...
    }
}
/*
package com.healthcare.gateway.filter;

import io.github.bucket4j.*;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Supplier;

@Component
public class RateLimiterFilter extends AbstractGatewayFilterFactory<RateLimiterFilter.Config> {

    private final ProxyManager<String> proxyManager;

    public RateLimiterFilter() {
        super(Config.class);

        // Create Redis client and connection using byte[] for keys
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<byte[], byte[]> connection = redisClient.connect(ByteArrayCodec.INSTANCE);

        // Create byte[] proxy manager
        LettuceBasedProxyManager<String> stringProxyManager = LettuceBasedProxyManager
                .<String> builderFor(connection) // explicitly tell the builder the key type is String
                .withKeyMapper(
                        key -> key.getBytes(StandardCharsets.UTF_8),         // String to byte[]
                        (byte[] bytes) -> new String(bytes, StandardCharsets.UTF_8)   // byte[] to String
                )
                .withExpirationStrategy((key, config) -> Duration.ofMinutes(1).toMillis())
                .build();


        // Convert byte[] key manager to string key manager
        this.proxyManager = stringProxyManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String ipAddress = extractClientIP(exchange);

            // Provide rate limit configuration for each IP
            Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                    .addLimit(Bandwidth.classic(
                            config.getCapacity(),
                            Refill.greedy(config.getCapacity(), Duration.ofMinutes(1))
                    ))
                    .build();

            return Mono.fromFuture(proxyManager.builder().build(ipAddress, configSupplier).tryConsume(1))
                    .flatMap(consumed -> {
                        if (consumed) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return exchange.getResponse().setComplete();
                        }
                    });
        };
    }

    private String extractClientIP(ServerWebExchange exchange) {
        // Extract IP from request (handle proxies if needed)
        return exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();
    }

    public static class Config {
        private int capacity = 100;

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
    }
}*/

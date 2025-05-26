package com.healthcare.account.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class LoadBalancerConfig {

    @Bean
    public ServerList<Server> ribbonServerList() {
        // Configure multiple instances of your services
        List<Server> list = new ArrayList<>();
        list.add(new Server("localhost", 8081)); // instance 1
        list.add(new Server("localhost", 8082)); // instance 2
        return new StaticServerList<>(list.toArray(new Server[0]));
    }

    @Bean
    public IRule ribbonRule() {
        return new RoundRobinRule(); // Use round-robin load balancing
    }
}
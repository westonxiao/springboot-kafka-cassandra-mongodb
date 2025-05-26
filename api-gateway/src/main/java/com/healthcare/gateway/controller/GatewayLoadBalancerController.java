package com.healthcare.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class GatewayLoadBalancerController {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @RequestMapping("/{path}")
    public ResponseEntity<String> route(@PathVariable String path, HttpServletRequest request) throws IOException {
        ServiceInstance instance = loadBalancer.choose("api-gateway");
        String url = instance.getUri() + "/" + path;

        // Forward the request
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                url,
                HttpMethod.valueOf(request.getMethod()),
                new HttpEntity<>(request.getInputStream()),
                String.class
        );
    }
}
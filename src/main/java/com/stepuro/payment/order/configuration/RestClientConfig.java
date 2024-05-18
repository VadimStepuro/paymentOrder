package com.stepuro.payment.order.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${client.timout}")
    private Duration clientTimeout;

    @Bean
    public RestTemplate statementClientRestTemplate(
            final RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder
                .setConnectTimeout(clientTimeout)
                .setReadTimeout(clientTimeout)
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .build();
    }
}

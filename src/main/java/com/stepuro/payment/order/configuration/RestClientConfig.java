package com.stepuro.payment.order.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${webclient.max.size}")
    private int size;

    @Value("${webclient.base.url.customer}")
    private String baseCustomerUrl;

    @Bean
    public RestClient restTemplate(){

        return RestClient.builder()
                .baseUrl(baseCustomerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

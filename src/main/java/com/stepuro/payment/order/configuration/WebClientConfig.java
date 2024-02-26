package com.stepuro.payment.order.configuration;

import com.stepuro.payment.order.api.exceptions.ClientException;
import com.stepuro.payment.order.api.exceptions.ServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {
    @Value("${webclient.max.size}")
    private int size;

    @Value("${webclient.base.url.customer}")
    private String baseCustomerUrl;

    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ServerException(errorBody)));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ClientException(errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
    @Bean
    public WebClient webclient() {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return WebClient
                .builder()
                .filter(errorHandler())
                .baseUrl(baseCustomerUrl)
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}

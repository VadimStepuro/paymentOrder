package com.stepuro.payment.order.client;

import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.TransferEntity;
import com.stepuro.payment.order.api.exceptions.ClientException;
import com.stepuro.payment.order.api.exceptions.ServerException;
import com.stepuro.payment.order.utils.wrapper.JacksonSerializeWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class CustomerWebClient {
    private final WebClient webClient;

    @Value("${webclient.base.url.customer}")
    private String baseCustomerUrl;

    public CustomerWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void sendTransfer(TransferEntity transferEntity, String urlStart){
        var url = UriComponentsBuilder.fromUriString(baseCustomerUrl)
                .pathSegment(urlStart, "transfer_amount")
                .toUriString();

        webClient.put()
                .uri(url)
                .body(Mono.just(transferEntity), TransferEntity.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> {
                            throw Objects.requireNonNull(error.bodyToMono(ApiError.class)
                                    .map( ex -> new ClientException(ex.getMessage())).block());
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> {
                            throw Objects.requireNonNull(error.bodyToMono(ApiError.class)
                                    .map( ex -> new ServerException(ex.getMessage())).block());
                        })
                .bodyToMono(Void.class)
                .block();
    }
}

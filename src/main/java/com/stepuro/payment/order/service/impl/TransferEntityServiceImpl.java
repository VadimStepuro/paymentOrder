package com.stepuro.payment.order.service.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.dto.TransferEntity;
import com.stepuro.payment.order.api.exceptions.ClientException;
import com.stepuro.payment.order.api.exceptions.ServerException;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import com.stepuro.payment.order.service.PaymentOrderEntityService;
import com.stepuro.payment.order.service.TransferEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class TransferEntityServiceImpl implements TransferEntityService {
    @Autowired
    private WebClient webClient;

    @Autowired
    private RestClient restClient;

    @Autowired
    private PaymentOrderEntityService paymentOrderEntityService;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "paymentOrderEntities", allEntries = true)
    public PaymentOrderEntityDto createRestClientPayment(PaymentOrderEntityDto paymentOrderEntityDto){
        TransferEntity transferEntity = createTransferEntity(paymentOrderEntityDto);

        switch (paymentOrderEntityDto.getPaymentType()){
            case PaymentType.CARD -> sendRestTransfer(transferEntity, paymentOrderEntityDto, "cards");

            case PaymentType.ACCOUNT -> sendRestTransfer(transferEntity, paymentOrderEntityDto, "accounts");
        }

        paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.APPROVED);

        return paymentOrderEntityService.create(paymentOrderEntityDto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "paymentOrderEntities", allEntries = true)
    public PaymentOrderEntityDto createPayment(PaymentOrderEntityDto paymentOrderEntityDto){
        TransferEntity transferEntity = createTransferEntity(paymentOrderEntityDto);

        switch (paymentOrderEntityDto.getPaymentType()){
            case PaymentType.CARD -> sendTransfer(transferEntity, paymentOrderEntityDto, "cards");

            case PaymentType.ACCOUNT -> sendTransfer(transferEntity, paymentOrderEntityDto, "accounts");
        }

        paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.APPROVED);

        return paymentOrderEntityService.create(paymentOrderEntityDto);
    }

    private TransferEntity createTransferEntity(PaymentOrderEntityDto paymentOrderEntityDto){
        if(paymentOrderEntityDto.getSourceAccountNumber() != null &&
                paymentOrderEntityDto.getDestinationAccountNumber() != null){
            return TransferEntity.builder()
                    .sourceNumber(paymentOrderEntityDto.getSourceAccountNumber())
                    .destinationNumber(paymentOrderEntityDto.getDestinationAccountNumber())
                    .userId(paymentOrderEntityDto.getUserId())
                    .amount(paymentOrderEntityDto.getAmount())
                    .build();
        }

        else {
            return TransferEntity.builder()
                    .sourceNumber(paymentOrderEntityDto.getSourceCardNumber())
                    .destinationNumber(paymentOrderEntityDto.getDestinationCardNumber())
                    .userId(paymentOrderEntityDto.getUserId())
                    .amount(paymentOrderEntityDto.getAmount())
                    .build();
        }
    }

    @Transactional
    private void sendTransfer(TransferEntity transferEntity, PaymentOrderEntityDto paymentOrderEntityDto, String urlStart){
        try {
            webClient.put()
                    .uri("/" + urlStart + "/transfer_amount")
                    .body(Mono.just(transferEntity), TransferEntity.class)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .ifNoneMatch("*")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> {
                                throw Objects.requireNonNull(error.bodyToMono(ApiError.class)
                                        .map((ex) -> new ClientException(ex.getMessage())).block());
                            })
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> {
                                throw Objects.requireNonNull(error.bodyToMono(ApiError.class)
                                        .map((ex) -> new ServerException(ex.getMessage())).block());
                            })
                    .bodyToMono(Void.class)
                    .block();
        }catch (ClientException|ServerException ex){
            paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);

            paymentOrderEntityService.create(paymentOrderEntityDto);

            throw ex;
        }
    }

    @Transactional
    private void sendRestTransfer(TransferEntity transferEntity, PaymentOrderEntityDto paymentOrderEntityDto, String urlStart){
        try {
            restClient.put()
                    .uri("/" + urlStart + "/transfer_amount")
                    .body(transferEntity)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .ifNoneMatch("*")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (request, response) -> {
                                JsonMapper mapper = new JsonMapper();

                                ApiError error = mapper.readValue(new String(response.getBody().readAllBytes()), ApiError.class);

                                throw new ClientException(error.getMessage());
                            })
                    .onStatus(HttpStatusCode::is5xxServerError,
                            (request, response) -> {
                                JsonMapper mapper = new JsonMapper();

                                ApiError error = mapper.readValue(new String(response.getBody().readAllBytes()), ApiError.class);

                                throw new ServerException(error.getMessage());
                            })
                    .toBodilessEntity();
        }catch (ClientException|ServerException ex){
            paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);

            paymentOrderEntityService.create(paymentOrderEntityDto);

            throw ex;
        }
    }
}

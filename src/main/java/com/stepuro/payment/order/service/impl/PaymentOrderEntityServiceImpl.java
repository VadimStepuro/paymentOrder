package com.stepuro.payment.order.service.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.dto.TransferEntity;
import com.stepuro.payment.order.api.exceptions.ClientException;
import com.stepuro.payment.order.api.exceptions.NoContentException;
import com.stepuro.payment.order.api.exceptions.ResourceNotFoundException;
import com.stepuro.payment.order.api.exceptions.ServerException;
import com.stepuro.payment.order.api.mapper.PaymentOrderEntityMapper;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import com.stepuro.payment.order.repository.PaymentOrderEntityRepositoryJpa;
import com.stepuro.payment.order.service.PaymentOrderEntityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentOrderEntityServiceImpl implements PaymentOrderEntityService {
    @Autowired
    private PaymentOrderEntityRepositoryJpa paymentOrderEntityRepositoryJpa;

    @Autowired
    private WebClient webClient;

    @Autowired
    private RestClient restClient;

    @Override
    public List<PaymentOrderEntityDto> findALl() {
        List<PaymentOrderEntityDto> paymentOrderEntityDtos = paymentOrderEntityRepositoryJpa
                .findAll()
                .stream()
                .map(PaymentOrderEntityMapper.INSTANCE::paymentOrderEntityToPaymentOrderEntityDto)
                .toList();

        if(paymentOrderEntityDtos.isEmpty()){
            throw new NoContentException("No PaymentOrderEntity found");
        }

        return paymentOrderEntityDtos;
    }

    @Override
    public PaymentOrderEntityDto findById(UUID id) {
        return PaymentOrderEntityMapper
                .INSTANCE
                .paymentOrderEntityToPaymentOrderEntityDto(
                        paymentOrderEntityRepositoryJpa
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("PaymentOrderEntity with id " + id + " not found"))
                );
    }

    @Override
    @Transactional
    public PaymentOrderEntityDto createPayment(PaymentOrderEntityDto paymentOrderEntityDto){
        TransferEntity transferEntity = createTransferEntity(paymentOrderEntityDto);

        switch (paymentOrderEntityDto.getPaymentType()){
            case PaymentType.CARD -> sendTransfer(transferEntity, paymentOrderEntityDto, "cards");

            case PaymentType.ACCOUNT -> sendTransfer(transferEntity, paymentOrderEntityDto, "accounts");
        }

        paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.APPROVED);

        return PaymentOrderEntityMapper
                .INSTANCE
                .paymentOrderEntityToPaymentOrderEntityDto(paymentOrderEntityRepositoryJpa
                        .save(PaymentOrderEntityMapper
                                .INSTANCE
                                .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto)));
    }

    @Override
    public PaymentOrderEntityDto createRestClientPayment(PaymentOrderEntityDto paymentOrderEntityDto){
        TransferEntity transferEntity = createTransferEntity(paymentOrderEntityDto);

        switch (paymentOrderEntityDto.getPaymentType()){
            case PaymentType.CARD -> sendRestTransfer(transferEntity, paymentOrderEntityDto, "cards");

            case PaymentType.ACCOUNT -> sendRestTransfer(transferEntity, paymentOrderEntityDto, "accounts");
        }

        paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.APPROVED);

        return PaymentOrderEntityMapper
                .INSTANCE
                .paymentOrderEntityToPaymentOrderEntityDto(paymentOrderEntityRepositoryJpa
                        .save(PaymentOrderEntityMapper
                                .INSTANCE
                                .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto)));
    }

    @Override
    @Transactional
    public PaymentOrderEntityDto create(PaymentOrderEntityDto paymentOrderEntityDto) {
        return PaymentOrderEntityMapper
                .INSTANCE
                .paymentOrderEntityToPaymentOrderEntityDto(
                        paymentOrderEntityRepositoryJpa
                                .save(PaymentOrderEntityMapper
                                        .INSTANCE
                                        .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto))
                );
    }

    @Override
    @Transactional
    public PaymentOrderEntityDto edit(PaymentOrderEntityDto paymentOrderEntityDto) {
        PaymentOrderEntity paymentOrderEntity = paymentOrderEntityRepositoryJpa
                .findById(paymentOrderEntityDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentOrderEntity with id " + paymentOrderEntityDto.getId() + " not found"));

        paymentOrderEntity.setSourceAccountNumber(paymentOrderEntityDto.getSourceAccountNumber());
        paymentOrderEntity.setDestinationAccountNumber(paymentOrderEntityDto.getDestinationAccountNumber());

        paymentOrderEntity.setSourceCardNumber(paymentOrderEntityDto.getSourceCardNumber());
        paymentOrderEntity.setDestinationCardNumber(paymentOrderEntityDto.getDestinationCardNumber());

        paymentOrderEntity.setCreatedDate(paymentOrderEntityDto.getCreatedDate());
        paymentOrderEntity.setUpdatedDate(paymentOrderEntityDto.getUpdatedDate());
        paymentOrderEntity.setStatus(paymentOrderEntityDto.getStatus());
        paymentOrderEntity.setPaymentType(paymentOrderEntityDto.getPaymentType());
        paymentOrderEntity.setAmount(paymentOrderEntityDto.getAmount());
        paymentOrderEntity.setUserId(paymentOrderEntityDto.getUserId());

        return PaymentOrderEntityMapper
                .INSTANCE
                .paymentOrderEntityToPaymentOrderEntityDto(
                        paymentOrderEntityRepositoryJpa.save(paymentOrderEntity)
                );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        paymentOrderEntityRepositoryJpa.deleteById(id);
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

    private void sendTransfer(TransferEntity transferEntity, PaymentOrderEntityDto paymentOrderEntityDto, String urlStart){
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
                            paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);
                            paymentOrderEntityRepositoryJpa
                                    .save(PaymentOrderEntityMapper
                                            .INSTANCE
                                            .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto));
                            return error.bodyToMono(ApiError.class).map((ex) -> new ClientException(ex.getMessage()));
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> {
                            paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);
                            paymentOrderEntityRepositoryJpa
                                    .save(PaymentOrderEntityMapper
                                            .INSTANCE
                                            .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto));
                            return error.bodyToMono(ApiError.class).map((ex) -> new ServerException(ex.getMessage()));
                        })
                .bodyToMono(Void.class)
                .block();
    }

    private void sendRestTransfer(TransferEntity transferEntity, PaymentOrderEntityDto paymentOrderEntityDto, String urlStart){
        restClient.put()
                    .uri("/" + urlStart + "/transfer_amount")
                    .body(transferEntity)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .ifNoneMatch("*")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (request, response) -> {
                                paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);
                                paymentOrderEntityRepositoryJpa
                                        .save(PaymentOrderEntityMapper
                                                .INSTANCE
                                                .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto));

                                JsonMapper mapper = new JsonMapper();

                                ApiError error = mapper.readValue(new String(response.getBody().readAllBytes()), ApiError.class);

                                throw new ClientException(error.getMessage());
                            })
                    .onStatus(HttpStatusCode::is5xxServerError,
                            (request, response) -> {
                                paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);
                                paymentOrderEntityRepositoryJpa
                                        .save(PaymentOrderEntityMapper
                                                .INSTANCE
                                                .paymentOrderEntityDtoToPaymentOrderEntity(paymentOrderEntityDto));

                                JsonMapper mapper = new JsonMapper();
                                mapper.registerModule(new JavaTimeModule());

                                ApiError error = mapper.readValue(new String(response.getBody().readAllBytes()), ApiError.class);

                                throw new ServerException(error.getMessage());
                            })
                    .toBodilessEntity();
    }
}


package com.stepuro.payment.order.service.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.dto.TransferEntity;
import com.stepuro.payment.order.api.exceptions.ClientException;
import com.stepuro.payment.order.api.exceptions.ServerException;
import com.stepuro.payment.order.client.CustomerRestClient;
import com.stepuro.payment.order.client.CustomerWebClient;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import com.stepuro.payment.order.service.PaymentOrderEntityService;
import com.stepuro.payment.order.service.TransferEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class TransferEntityServiceImpl implements TransferEntityService {

    private final PaymentOrderEntityService paymentOrderEntityService;
    private final CustomerRestClient customerRestClient;
    private final CustomerWebClient customerWebClient;

    public TransferEntityServiceImpl(
            CustomerRestClient customerRestClient,
            PaymentOrderEntityService paymentOrderEntityService,
            CustomerWebClient customerWebClient
    ) {
        this.customerRestClient = customerRestClient;
        this.paymentOrderEntityService = paymentOrderEntityService;
        this.customerWebClient = customerWebClient;
    }

    @Override
    @Transactional(noRollbackFor = {ClientException.class, ServerException.class})
    @CacheEvict(cacheNames = "paymentOrderEntities", allEntries = true)
    public PaymentOrderEntityDto createRestClientPayment(PaymentOrderEntityDto paymentOrderEntityDto){
        TransferEntity transferEntity = createTransferEntity(paymentOrderEntityDto);

        if (paymentOrderEntityDto.getPaymentType().equals(PaymentType.CARD))
            sendRestTransfer(transferEntity, paymentOrderEntityDto, "cards");

        if(paymentOrderEntityDto.getPaymentType().equals(PaymentType.ACCOUNT))
            sendRestTransfer(transferEntity, paymentOrderEntityDto, "accounts");

        paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.APPROVED);

        return paymentOrderEntityService.create(paymentOrderEntityDto);
    }

    @Override
    @Transactional(noRollbackFor = {ClientException.class, ServerException.class})
    @CacheEvict(cacheNames = "paymentOrderEntities", allEntries = true)
    public PaymentOrderEntityDto createPayment(PaymentOrderEntityDto paymentOrderEntityDto){
        TransferEntity transferEntity = createTransferEntity(paymentOrderEntityDto);

        if (paymentOrderEntityDto.getPaymentType().equals(PaymentType.CARD))
            sendTransfer(transferEntity, paymentOrderEntityDto, "cards");

        if (paymentOrderEntityDto.getPaymentType().equals(PaymentType.ACCOUNT))
            sendTransfer(transferEntity, paymentOrderEntityDto, "accounts");

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

    private void sendTransfer(TransferEntity transferEntity, PaymentOrderEntityDto paymentOrderEntityDto, String urlStart){
        try {
            customerWebClient.sendTransfer(transferEntity, urlStart);
        }
        catch (ClientException|ServerException ex){
            paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);

            paymentOrderEntityService.create(paymentOrderEntityDto);

            throw ex;
        }
    }

    private void sendRestTransfer(TransferEntity transferEntity, PaymentOrderEntityDto paymentOrderEntityDto, String urlStart) {
        try {
            customerRestClient.sendRestTransfer(transferEntity, urlStart);
        }
        catch (ClientException | ServerException ex) {
            paymentOrderEntityDto.setStatus(PaymentOrderEntityStatus.ERROR);

            paymentOrderEntityService.create(paymentOrderEntityDto);

            throw ex;
        }
    }
}

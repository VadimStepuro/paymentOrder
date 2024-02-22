package com.stepuro.payment.order.service.impl;

import com.stepuro.payment.order.api.dto.CreatePaymentRequest;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.exceptions.NoContentException;
import com.stepuro.payment.order.api.exceptions.ResourceNotFoundException;
import com.stepuro.payment.order.api.mapper.PaymentOrderEntityMapper;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentType;
import com.stepuro.payment.order.repository.PaymentOrderEntityRepositoryJpa;
import com.stepuro.payment.order.service.PaymentOrderEntityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentOrderEntityServiceImpl implements PaymentOrderEntityService {
    @Autowired
    private PaymentOrderEntityRepositoryJpa paymentOrderEntityRepositoryJpa;

    @Autowired
    private WebClient webClient;

    @Override
    @Transactional
    public PaymentOrderEntityDto createPayment(CreatePaymentRequest request){
        return null;
    }



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
    public boolean checkIfPaymentExists(String paymentNumber, PaymentType paymentType) {
        Boolean result;

        switch (paymentType) {
            case PaymentType.CARD:
                result = webClient.get()
                        .uri("http://localhost:8080/cards/exists_by_card_number/" + paymentNumber)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block(Duration.of(1000, ChronoUnit.MILLIS));
                break;

            case PaymentType.ACCOUNT:
                result = webClient.get()
                        .uri("http://localhost:8080/accounts/exists_by_account_number/" + paymentNumber)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block(Duration.of(1000, ChronoUnit.MILLIS));
                break;

            default:
                return false;
        }
        return result;
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
}

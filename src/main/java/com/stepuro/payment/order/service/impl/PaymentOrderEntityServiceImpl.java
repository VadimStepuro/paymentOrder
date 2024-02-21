package com.stepuro.payment.order.service.impl;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.exceptions.NoContentException;
import com.stepuro.payment.order.api.exceptions.ResourceNotFoundException;
import com.stepuro.payment.order.api.mapper.PaymentOrderEntityMapper;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.repository.PaymentOrderEntityRepositoryJpa;
import com.stepuro.payment.order.service.PaymentOrderEntityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentOrderEntityServiceImpl implements PaymentOrderEntityService {
    @Autowired
    private PaymentOrderEntityRepositoryJpa paymentOrderEntityRepositoryJpa;

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

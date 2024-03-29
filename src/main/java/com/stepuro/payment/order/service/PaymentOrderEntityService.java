package com.stepuro.payment.order.service;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;

import java.util.List;
import java.util.UUID;

public interface PaymentOrderEntityService {
    List<PaymentOrderEntityDto> findAll();

    PaymentOrderEntityDto findById(UUID id);

    PaymentOrderEntityDto create(PaymentOrderEntityDto paymentOrderEntityDto);

    PaymentOrderEntityDto edit(PaymentOrderEntityDto paymentOrderEntityDto);

    void delete(UUID id);
}

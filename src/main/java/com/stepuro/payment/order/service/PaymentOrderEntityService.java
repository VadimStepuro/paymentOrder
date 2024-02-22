package com.stepuro.payment.order.service;

import com.stepuro.payment.order.api.dto.CreatePaymentRequest;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.model.enums.PaymentType;

import java.util.List;
import java.util.UUID;

public interface PaymentOrderEntityService {
    PaymentOrderEntityDto createPayment(CreatePaymentRequest request);

    List<PaymentOrderEntityDto> findALl();

    PaymentOrderEntityDto findById(UUID id);

    boolean checkIfPaymentExists(String paymentNumber, PaymentType paymentType);


    PaymentOrderEntityDto create(PaymentOrderEntityDto paymentOrderEntityDto);

    PaymentOrderEntityDto edit(PaymentOrderEntityDto paymentOrderEntityDto);

    void delete(UUID id);
}

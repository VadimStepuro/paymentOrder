package com.stepuro.payment.order.service;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;

public interface TransferEntityService {
    PaymentOrderEntityDto createRestClientPayment(PaymentOrderEntityDto paymentOrderEntityDto);
    PaymentOrderEntityDto createPayment(PaymentOrderEntityDto paymentOrderEntityDto);
}

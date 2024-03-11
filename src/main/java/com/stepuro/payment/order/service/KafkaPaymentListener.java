package com.stepuro.payment.order.service;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;

public interface KafkaPaymentListener {
    void paymentListener(PaymentOrderEntityDto paymentOrderEntityDto);
}

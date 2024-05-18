package com.stepuro.payment.order.service.impl;

import com.stepuro.payment.order.api.annotation.Loggable;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.service.KafkaPaymentListener;
import com.stepuro.payment.order.service.TransferEntityService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaPaymentListenerImpl implements KafkaPaymentListener {
    private final TransferEntityService transferEntityService;

    public KafkaPaymentListenerImpl(TransferEntityService transferEntityService) {
        this.transferEntityService = transferEntityService;
    }

    @KafkaListener(topics = "${kafka.topic-name.transfer-amount}",
    containerFactory = "kafkaListenerContainerFactory")
    @Loggable
    public void paymentListener(PaymentOrderEntityDto paymentOrderEntityDto){
        transferEntityService.createRestClientPayment(paymentOrderEntityDto);
    }
}

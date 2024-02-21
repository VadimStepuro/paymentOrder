package com.stepuro.payment.order.repository.sample;

import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

public class PaymentOrderEntitySample {
    public static PaymentOrderEntity paymentOrderEntity1 = PaymentOrderEntity.builder()
            .sourceCardNumber("5425233430109903")
            .destinationCardNumber("5425233430109333")
            .status(PaymentOrderEntityStatus.APPROVED)
            .createdDate(Timestamp.from(Instant.now()))
            .updatedDate(Timestamp.from(Instant.now()))
            .paymentType(PaymentType.CARD)
            .amount(new BigDecimal("300.05"))
            .build();

    public static PaymentOrderEntity paymentOrderEntity2 = PaymentOrderEntity.builder()
            .sourceAccountNumber("IE12BOFI90000112345678")
            .destinationAccountNumber("IE12BOFI90000112345555")
            .status(PaymentOrderEntityStatus.APPROVED)
            .createdDate(Timestamp.from(Instant.now()))
            .updatedDate(Timestamp.from(Instant.now()))
            .paymentType(PaymentType.ACCOUNT)
            .amount(new BigDecimal("300.05"))
            .build();

    public static PaymentOrderEntity paymentOrderEntity3 = PaymentOrderEntity.builder()
            .sourceAccountNumber("IE12BOFI90000112345678")
            .destinationAccountNumber("IE12BOFI90000112345555")
            .status(PaymentOrderEntityStatus.APPROVED)
            .createdDate(Timestamp.from(Instant.now()))
            .updatedDate(Timestamp.from(Instant.now()))
            .paymentType(PaymentType.ACCOUNT)
            .amount(new BigDecimal("300.05"))
            .build();

    public static PaymentOrderEntity paymentOrderEntity4 = PaymentOrderEntity.builder()
            .sourceCardNumber("5425233430109903")
            .destinationCardNumber("5425233430109333")
            .status(PaymentOrderEntityStatus.APPROVED)
            .createdDate(Timestamp.from(Instant.now()))
            .updatedDate(Timestamp.from(Instant.now()))
            .paymentType(PaymentType.CARD)
            .amount(new BigDecimal("300.05"))
            .build();
}

package com.stepuro.payment.order.repository;

import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.repository.sample.PaymentOrderEntitySample;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.stepuro.payment.order.repository.sample.PaymentOrderEntitySample.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PaymentOrderEntityRepositoryJpaTests {
    @Autowired
    private PaymentOrderEntityRepositoryJpa paymentOrderEntityRepositoryJpa;

    @Test
    public void PaymentOrderEntityRepositoryJpa_Save_ReturnsSavedModel(){
        PaymentOrderEntity savedEntity = paymentOrderEntityRepositoryJpa.save(paymentOrderEntity1);

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(paymentOrderEntity1.getSourceCardNumber(), savedEntity.getSourceCardNumber());
        assertEquals(paymentOrderEntity1.getDestinationCardNumber(), savedEntity.getDestinationCardNumber());
        assertEquals(paymentOrderEntity1.getSourceAccountNumber(), savedEntity.getSourceAccountNumber());
        assertEquals(paymentOrderEntity1.getDestinationAccountNumber(), savedEntity.getDestinationAccountNumber());
        assertEquals(paymentOrderEntity1.getCreatedDate(), savedEntity.getCreatedDate());
        assertEquals(paymentOrderEntity1.getUpdatedDate(), savedEntity.getUpdatedDate());
        assertEquals(paymentOrderEntity1.getPaymentType(), savedEntity.getPaymentType());
        assertEquals(paymentOrderEntity1.getStatus(), savedEntity.getStatus());
        assertEquals(paymentOrderEntity1.getAmount(), savedEntity.getAmount());
    }
}

package com.stepuro.payment.order.repository;

import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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

    @Test
    public void PaymentOrderEntityRepositoryJpa_FindAll_ReturnsModels(){
        paymentOrderEntityRepositoryJpa.saveAll(paymentOrderEntityList);

        List<PaymentOrderEntity> paymentOrderEntities = paymentOrderEntityRepositoryJpa.findAll();

        assertNotNull(paymentOrderEntities);
        assertEquals(4, paymentOrderEntities.size());
    }

    @Test
    public void PaymentOrderEntityRepositoryJpa_FindById_ReturnsModel(){
        PaymentOrderEntity savedEntity = paymentOrderEntityRepositoryJpa.save(paymentOrderEntity1);
        paymentOrderEntityRepositoryJpa.save(paymentOrderEntity2);
        
        PaymentOrderEntity foundEntity = paymentOrderEntityRepositoryJpa.findById(savedEntity.getId()).get();

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(savedEntity.getSourceCardNumber(), foundEntity.getSourceCardNumber());
        assertEquals(savedEntity.getDestinationCardNumber(), foundEntity.getDestinationCardNumber());
        assertEquals(savedEntity.getSourceAccountNumber(), foundEntity.getSourceAccountNumber());
        assertEquals(savedEntity.getDestinationAccountNumber(), foundEntity.getDestinationAccountNumber());
        assertEquals(savedEntity.getCreatedDate(), foundEntity.getCreatedDate());
        assertEquals(savedEntity.getUpdatedDate(), foundEntity.getUpdatedDate());
        assertEquals(savedEntity.getPaymentType(), foundEntity.getPaymentType());
        assertEquals(savedEntity.getStatus(), foundEntity.getStatus());
        assertEquals(savedEntity.getAmount(), foundEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityRepositoryJpa_Update_ChangesModel(){
        PaymentOrderEntity savedEntity = paymentOrderEntityRepositoryJpa.save(paymentOrderEntity1);

        savedEntity.setSourceAccountNumber("IE12BOFI90000112345555");
        savedEntity.setDestinationAccountNumber("IE12BOFI90000112345666");

        savedEntity.setSourceCardNumber("5425233430109966");
        savedEntity.setDestinationCardNumber("5425233430109999");

        savedEntity.setCreatedDate(Timestamp.from(Instant.now().minusSeconds(60 * 60 * 24 * 100)));
        savedEntity.setUpdatedDate(Timestamp.from(Instant.now().minusSeconds(60 * 60 * 24 * 50)));
        savedEntity.setStatus(PaymentOrderEntityStatus.IN_PROGRESS);
        savedEntity.setPaymentType(PaymentType.ACCOUNT);
        savedEntity.setAmount(new BigDecimal("200.50"));

        PaymentOrderEntity updatedEntity = paymentOrderEntityRepositoryJpa.save(savedEntity);

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(savedEntity.getSourceCardNumber(), updatedEntity.getSourceCardNumber());
        assertEquals(savedEntity.getDestinationCardNumber(), updatedEntity.getDestinationCardNumber());
        assertEquals(savedEntity.getSourceAccountNumber(), updatedEntity.getSourceAccountNumber());
        assertEquals(savedEntity.getDestinationAccountNumber(), updatedEntity.getDestinationAccountNumber());
        assertEquals(savedEntity.getCreatedDate(), updatedEntity.getCreatedDate());
        assertEquals(savedEntity.getUpdatedDate(), updatedEntity.getUpdatedDate());
        assertEquals(savedEntity.getPaymentType(), updatedEntity.getPaymentType());
        assertEquals(savedEntity.getStatus(), updatedEntity.getStatus());
        assertEquals(savedEntity.getAmount(), updatedEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityRepositoryJpa_Remove_RemovesModel(){
        PaymentOrderEntity savedEntity = paymentOrderEntityRepositoryJpa.save(paymentOrderEntity1);

        paymentOrderEntityRepositoryJpa.deleteById(savedEntity.getId());

        boolean existsById = paymentOrderEntityRepositoryJpa.existsById(savedEntity.getId());

        assertFalse(existsById);
    }
}

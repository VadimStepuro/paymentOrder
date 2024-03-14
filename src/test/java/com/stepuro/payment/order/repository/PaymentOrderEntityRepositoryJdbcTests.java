package com.stepuro.payment.order.repository;

import com.stepuro.payment.order.api.exceptions.ResourceNotFoundException;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static com.stepuro.payment.order.repository.sample.PaymentOrderEntitySample.paymentOrderEntity1;
import static com.stepuro.payment.order.repository.sample.PaymentOrderEntitySample.paymentOrderEntity2;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
public class PaymentOrderEntityRepositoryJdbcTests {
    private PaymentOrderEntityRepositoryJdbc paymentOrderEntityRepositoryJdbc;
    private EmbeddedDatabase embeddedDatabase;

    @BeforeEach
    public void setup(){
        embeddedDatabase = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("/sql/01.create-payment-order-entity.sql")
                .build();

        paymentOrderEntityRepositoryJdbc = new PaymentOrderEntityRepositoryJdbc();
        paymentOrderEntityRepositoryJdbc.setDataSource(embeddedDatabase);
    }

    @AfterEach
    public void tearDown(){
        embeddedDatabase.shutdown();
    }

    @Test
    public void PaymentOrderEntityRepositoryJdbc_Save_SavesModel(){

        UUID id = paymentOrderEntityRepositoryJdbc.save(paymentOrderEntity1);

        PaymentOrderEntity savedEntity = paymentOrderEntityRepositoryJdbc.findById(id);

        assertNotNull(id);
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(id, savedEntity.getId());
        assertEquals(paymentOrderEntity1.getSourceCardNumber(), savedEntity.getSourceCardNumber());
        assertEquals(paymentOrderEntity1.getDestinationCardNumber(), savedEntity.getDestinationCardNumber());
        assertEquals(paymentOrderEntity1.getSourceAccountNumber(), savedEntity.getSourceAccountNumber());
        assertEquals(paymentOrderEntity1.getDestinationAccountNumber(), savedEntity.getDestinationAccountNumber());
        assertEquals(paymentOrderEntity1.getCreatedDate().getTime(), savedEntity.getCreatedDate().getTime());
        assertEquals(paymentOrderEntity1.getUpdatedDate().getTime(), savedEntity.getUpdatedDate().getTime());
        assertEquals(paymentOrderEntity1.getPaymentType(), savedEntity.getPaymentType());
        assertEquals(paymentOrderEntity1.getStatus(), savedEntity.getStatus());
        assertEquals(paymentOrderEntity1.getAmount(), savedEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityRepositoryJdbc_FindById_ReturnsModel(){
        UUID id = paymentOrderEntityRepositoryJdbc.save(paymentOrderEntity1);
        paymentOrderEntityRepositoryJdbc.save(paymentOrderEntity2);

        PaymentOrderEntity foundEntity = paymentOrderEntityRepositoryJdbc.findById(id);

        assertNotNull(id);
        assertNotNull(foundEntity);
        assertNotNull(foundEntity.getId());
        assertEquals(id, foundEntity.getId());
        assertEquals(paymentOrderEntity1.getSourceCardNumber(), foundEntity.getSourceCardNumber());
        assertEquals(paymentOrderEntity1.getDestinationCardNumber(), foundEntity.getDestinationCardNumber());
        assertEquals(paymentOrderEntity1.getSourceAccountNumber(), foundEntity.getSourceAccountNumber());
        assertEquals(paymentOrderEntity1.getDestinationAccountNumber(), foundEntity.getDestinationAccountNumber());
        assertEquals(paymentOrderEntity1.getCreatedDate().getTime(), foundEntity.getCreatedDate().getTime());
        assertEquals(paymentOrderEntity1.getUpdatedDate().getTime(), foundEntity.getUpdatedDate().getTime());
        assertEquals(paymentOrderEntity1.getPaymentType(), foundEntity.getPaymentType());
        assertEquals(paymentOrderEntity1.getStatus(), foundEntity.getStatus());
        assertEquals(paymentOrderEntity1.getAmount(), foundEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityRepositoryJdbc_Edit_ChangesModel(){
        UUID id = paymentOrderEntityRepositoryJdbc.save(paymentOrderEntity1);

        PaymentOrderEntity savedEntity = paymentOrderEntityRepositoryJdbc.findById(id);

        savedEntity.setSourceAccountNumber("IE12BOFI90000112345555");
        savedEntity.setDestinationAccountNumber("IE12BOFI90000112345666");

        savedEntity.setSourceCardNumber("5425233430109966");
        savedEntity.setDestinationCardNumber("5425233430109999");

        savedEntity.setCreatedDate(Timestamp.from(Instant.now().minusSeconds(60 * 60 * 24 * 100)));
        savedEntity.setUpdatedDate(Timestamp.from(Instant.now().minusSeconds(60 * 60 * 24 * 50)));
        savedEntity.setStatus(PaymentOrderEntityStatus.IN_PROGRESS);
        savedEntity.setPaymentType(PaymentType.ACCOUNT);
        savedEntity.setAmount(new BigDecimal("200.50"));

        paymentOrderEntityRepositoryJdbc.edit(savedEntity);

        PaymentOrderEntity updatedEntity = paymentOrderEntityRepositoryJdbc.findById(savedEntity.getId());

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(savedEntity.getSourceCardNumber(), updatedEntity.getSourceCardNumber());
        assertEquals(savedEntity.getDestinationCardNumber(), updatedEntity.getDestinationCardNumber());
        assertEquals(savedEntity.getSourceAccountNumber(), updatedEntity.getSourceAccountNumber());
        assertEquals(savedEntity.getDestinationAccountNumber(), updatedEntity.getDestinationAccountNumber());
        assertEquals(savedEntity.getCreatedDate().getTime(), updatedEntity.getCreatedDate().getTime());
        assertEquals(savedEntity.getUpdatedDate().getTime(), updatedEntity.getUpdatedDate().getTime());
        assertEquals(savedEntity.getPaymentType(), updatedEntity.getPaymentType());
        assertEquals(savedEntity.getStatus(), updatedEntity.getStatus());
        assertEquals(savedEntity.getAmount(), updatedEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityRepositoryJdbc_Remove_RemovesModel(){
        UUID id = paymentOrderEntityRepositoryJdbc.save(paymentOrderEntity1);

        paymentOrderEntityRepositoryJdbc.deleteById(id);

        assertThrows(ResourceNotFoundException.class, () -> paymentOrderEntityRepositoryJdbc.findById(id));
    }
}

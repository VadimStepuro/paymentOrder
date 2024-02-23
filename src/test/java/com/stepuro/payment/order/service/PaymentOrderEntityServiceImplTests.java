package com.stepuro.payment.order.service;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.exceptions.ResourceNotFoundException;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import com.stepuro.payment.order.repository.PaymentOrderEntityRepositoryJpa;
import com.stepuro.payment.order.service.impl.PaymentOrderEntityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.stepuro.payment.order.service.sample.PaymentOrderEntitySample.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentOrderEntityServiceImplTests {
    @Mock
    private PaymentOrderEntityRepositoryJpa paymentOrderEntityRepositoryJpa;

    @InjectMocks
    private PaymentOrderEntityServiceImpl paymentOrderEntityService;

    @Test
    public void PaymentOrderEntityServiceImpl_Save_SavesModel(){

        when(paymentOrderEntityRepositoryJpa.save(any(PaymentOrderEntity.class))).thenReturn(paymentOrderEntity1);

        PaymentOrderEntityDto savedEntity = paymentOrderEntityService.create(paymentOrderEntityDto);

        assertNotNull(savedEntity);
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
    public void PaymentOrderEntityServiceImpl_FindAll_ReturnsAllModels(){
        when(paymentOrderEntityRepositoryJpa.findAll()).thenReturn(paymentOrderEntityList);

        List<PaymentOrderEntityDto> entityDtos = paymentOrderEntityService.findALl();

        assertNotNull(entityDtos);
        assertEquals(4, entityDtos.size());
    }

    @Test
    public void PaymentOrderEntityServiceImpl_FindById_ReturnsModel(){
        when(paymentOrderEntityRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.of(paymentOrderEntity1));

        PaymentOrderEntityDto foundEntity = paymentOrderEntityService.findById(UUID.randomUUID());

        assertNotNull(foundEntity);
        assertEquals(paymentOrderEntity1.getSourceCardNumber(), foundEntity.getSourceCardNumber());
        assertEquals(paymentOrderEntity1.getDestinationCardNumber(), foundEntity.getDestinationCardNumber());
        assertEquals(paymentOrderEntity1.getSourceAccountNumber(), foundEntity.getSourceAccountNumber());
        assertEquals(paymentOrderEntity1.getDestinationAccountNumber(), foundEntity.getDestinationAccountNumber());
        assertEquals(paymentOrderEntity1.getCreatedDate(), foundEntity.getCreatedDate());
        assertEquals(paymentOrderEntity1.getUpdatedDate(), foundEntity.getUpdatedDate());
        assertEquals(paymentOrderEntity1.getPaymentType(), foundEntity.getPaymentType());
        assertEquals(paymentOrderEntity1.getStatus(), foundEntity.getStatus());
        assertEquals(paymentOrderEntity1.getAmount(), foundEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityServiceImpl_Edit_ReturnsEditedModel(){
        when(paymentOrderEntityRepositoryJpa.save(any(PaymentOrderEntity.class))).thenReturn(paymentOrderEntity1);
        when(paymentOrderEntityRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.of(paymentOrderEntity1));

        PaymentOrderEntityDto savedEntity = paymentOrderEntityService.create(paymentOrderEntityDto);

        savedEntity.setSourceAccountNumber("IE12BOFI90000112345555");
        savedEntity.setDestinationAccountNumber("IE12BOFI90000112345666");

        savedEntity.setSourceCardNumber("5425233430109966");
        savedEntity.setDestinationCardNumber("5425233430109999");

        savedEntity.setCreatedDate(Timestamp.from(Instant.now().minusSeconds(60 * 60 * 24 * 100)));
        savedEntity.setUpdatedDate(Timestamp.from(Instant.now().minusSeconds(60 * 60 * 24 * 50)));
        savedEntity.setStatus(PaymentOrderEntityStatus.IN_PROGRESS);
        savedEntity.setPaymentType(PaymentType.ACCOUNT);
        savedEntity.setAmount(new BigDecimal("200.50"));

        PaymentOrderEntityDto editedEntity = paymentOrderEntityService.edit(savedEntity);

        assertNotNull(editedEntity);
        assertEquals(savedEntity.getSourceCardNumber(), editedEntity.getSourceCardNumber());
        assertEquals(savedEntity.getDestinationCardNumber(), editedEntity.getDestinationCardNumber());
        assertEquals(savedEntity.getSourceAccountNumber(), editedEntity.getSourceAccountNumber());
        assertEquals(savedEntity.getDestinationAccountNumber(), editedEntity.getDestinationAccountNumber());
        assertEquals(savedEntity.getCreatedDate(), editedEntity.getCreatedDate());
        assertEquals(savedEntity.getUpdatedDate(), editedEntity.getUpdatedDate());
        assertEquals(savedEntity.getPaymentType(), editedEntity.getPaymentType());
        assertEquals(savedEntity.getStatus(), editedEntity.getStatus());
        assertEquals(savedEntity.getAmount(), editedEntity.getAmount());
    }

    @Test
    public void PaymentOrderEntityServiceImpl_Delete_DeletesModel(){
        when(paymentOrderEntityRepositoryJpa.findById(any(UUID.class))).thenReturn(Optional.empty());
        doNothing().when(paymentOrderEntityRepositoryJpa).deleteById(any(UUID.class));

        UUID uuid = UUID.randomUUID();

        paymentOrderEntityService.delete(uuid);

        assertThrows(ResourceNotFoundException.class, () -> paymentOrderEntityService.findById(uuid));
    }
}

package com.stepuro.payment.order.repository;

import com.stepuro.payment.order.model.PaymentOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentOrderEntityRepositoryJpa extends JpaRepository<PaymentOrderEntity, UUID> {
}

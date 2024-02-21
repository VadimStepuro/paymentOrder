package com.stepuro.payment.order.api.mapper;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentOrderEntityMapper {
    PaymentOrderEntityMapper INSTANCE = Mappers.getMapper(PaymentOrderEntityMapper.class);

    PaymentOrderEntity paymentOrderEntityDtoToPaymentOrderEntity(PaymentOrderEntityDto paymentOrderEntityDto);

    PaymentOrderEntityDto paymentOrderEntityToPaymentOrderEntityDto(PaymentOrderEntity paymentOrderEntity);
}

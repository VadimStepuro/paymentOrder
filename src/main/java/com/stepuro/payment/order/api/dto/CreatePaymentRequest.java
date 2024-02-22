package com.stepuro.payment.order.api.dto;

import com.stepuro.payment.order.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setter
@Getter
public class CreatePaymentRequest {
    @NotNull
    private PaymentType paymentType;

    @CreditCardNumber(message = "Invalid source credit card number")
    private String sourceCardNumber;

    @CreditCardNumber(message = "Invalid destination credit card number")
    private String destinationCardNumber;

    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30}$", message = "Invalid source account number")
    private String sourceAccountNumber;

    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30}$", message = "Invalid destination account number")
    private String destinationAccountNumber;

    @Positive
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Integer userId;
}

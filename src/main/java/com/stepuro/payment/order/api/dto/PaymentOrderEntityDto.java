package com.stepuro.payment.order.api.dto;

import com.stepuro.payment.order.api.annotation.OperationDataValid;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setter
@Getter
@OperationDataValid(sourceCardNumber = "sourceCardNumber",
        destinationCardNumber = "destinationCardNumber",
        sourceAccountNumber = "sourceAccountNumber",
        destinationAccountNumber = "destinationAccountNumber",
        paymentType = "paymentType")
public class PaymentOrderEntityDto {
    private UUID id;

    @CreditCardNumber(message = "Invalid source credit card number")
    private String sourceCardNumber;

    @CreditCardNumber(message = "Invalid destination credit card number")
    private String destinationCardNumber;

    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30}$", message = "Invalid source account number")
    private String sourceAccountNumber;

    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30}$", message = "Invalid destination account number")
    private String destinationAccountNumber;

    @NotNull
    @PastOrPresent(message = "Created date can't be in future")
    private Timestamp createdDate;

    @PastOrPresent(message = "Updated date can't be in future")
    private Timestamp updatedDate;

    @NotNull(message = "Status can't be null")
    private PaymentOrderEntityStatus status;

    @NotNull(message = "Payment type can't be null")
    private PaymentType paymentType;

    @Positive
    @NotNull
    private BigDecimal amount;
}

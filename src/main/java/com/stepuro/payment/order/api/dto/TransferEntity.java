package com.stepuro.payment.order.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class TransferEntity {
    @NotNull
    @Pattern(regexp = "^(?:(?:4\\d{12}(?:\\d{3})?|" +
            "(?:5[1-5]\\d{2}|" +
            "222[1-9]|22[3-9]\\d|" +
            "2[3-6]\\d{2}|27[01]\\d|2720)\\d{12}|" +
            " 3[47]\\d{13}|" +
            " 3(?:0[0-5]|[68]\\d)\\d{11}|" +
            " 6(?:011|5\\d{2})\\d{12}|" +
            "(?:2131|1800|35\\d{3})\\d{11})|" +
            "[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30})$", message = "Source number is not valid")
    private String sourceNumber;

    @NotNull
    @Pattern(regexp = "^(?:(?:4\\d{12}(?:\\d{3})?|" +
            "(?:5[1-5]\\d{2}|" +
            "222[1-9]|22[3-9]\\d|" +
            "2[3-6]\\d{2}|27[01]\\d|2720)\\d{12}|" +
            " 3[47]\\d{13}|" +
            " 3(?:0[0-5]|[68]\\d)\\d{11}|" +
            " 6(?:011|5\\d{2})\\d{12}|" +
            "(?:2131|1800|35\\d{3})\\d{11})|" +
            "[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30})$", message = "Destination number is not valid")
    private String destinationNumber;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Integer userId;
}

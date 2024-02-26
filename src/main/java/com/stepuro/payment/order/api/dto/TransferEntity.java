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
    @Pattern(regexp = "^(?:4[0-9]{12}(?:[0-9]{3})? | " +
            "(?:5[1-5][0-9]{2} | " +
            "222[1-9]|22[3-9][0-9] |" +
            "2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12} |" +
            " 3[47][0-9]{13} |" +
            " 3(?:0[0-5]|[68][0-9])[0-9]{11} |" +
            " 6(?:011|5[0-9]{2})[0-9]{12} |" +
            "(?:2131|1800|35\\d{3})\\d{11}) |" +
            "[\\d+]{10,12}$", message = "Source number is not valid")
    private String sourceNumber;

    @NotNull
    @Pattern(regexp = "^(?:4[0-9]{12}(?:[0-9]{3})? | " +
            "(?:5[1-5][0-9]{2} | " +
            "222[1-9]|22[3-9][0-9] |" +
            "2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12} |" +
            " 3[47][0-9]{13} |" +
            " 3(?:0[0-5]|[68][0-9])[0-9]{11} |" +
            " 6(?:011|5[0-9]{2})[0-9]{12} |" +
            "(?:2131|1800|35\\d{3})\\d{11}) |" +
            "[\\d+]{10,12}$", message = "Destination number is not valid")
    private String destinationNumber;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Integer userId;
}

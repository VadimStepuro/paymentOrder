package com.stepuro.payment.order.api.annotation;

import com.stepuro.payment.order.utils.validator.OperationDataValidValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OperationDataValidValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationDataValid {
    String message() default "Operation data is invalid";

    String sourceCardNumber();
    String destinationCardNumber();
    String sourceAccountNumber();
    String destinationAccountNumber();
    String paymentType();


}

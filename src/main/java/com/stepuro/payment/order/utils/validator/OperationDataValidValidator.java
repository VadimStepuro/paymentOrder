package com.stepuro.payment.order.utils.validator;

import com.stepuro.payment.order.api.annotation.OperationDataValid;
import com.stepuro.payment.order.model.enums.PaymentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

public class OperationDataValidValidator implements ConstraintValidator<OperationDataValid, Object>{
    private String paymentTypeName;
    private String sourceCardNumberName;
    private String destinationCardNumberName;
    private String sourceAccountNumberName;
    private String destinationAccountNumberName;

    @Override
    public void initialize(OperationDataValid constraintAnnotation) {
        paymentTypeName = constraintAnnotation.paymentType();

        sourceCardNumberName = constraintAnnotation.sourceCardNumber();
        destinationCardNumberName = constraintAnnotation.destinationCardNumber();

        sourceAccountNumberName = constraintAnnotation.sourceAccountNumber();
        destinationAccountNumberName = constraintAnnotation.destinationAccountNumber();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        PaymentType paymentType = PaymentType
                .valueOf(Objects
                .requireNonNull(new BeanWrapperImpl(o).getPropertyValue(paymentTypeName))
                .toString());

        Object sourceCardNumber = new BeanWrapperImpl(o).getPropertyValue(sourceCardNumberName);
        Object destinationCardNumber = new BeanWrapperImpl(o).getPropertyValue(destinationCardNumberName);

        Object sourceAccountNumber = new BeanWrapperImpl(o).getPropertyValue(sourceAccountNumberName);
        Object destinationAccountNumber = new BeanWrapperImpl(o).getPropertyValue(destinationAccountNumberName);

        boolean areAccountNumbersPresent = sourceAccountNumber != null &&
                destinationAccountNumber != null &&
                !sourceAccountNumber.equals(destinationCardNumber);

        if(paymentType.equals(PaymentType.CARD)){
            return (sourceCardNumber != null &&
                    destinationCardNumber != null &&
                    !sourceCardNumber.equals(destinationCardNumber)) ||
                    areAccountNumbersPresent;
        }
        else {
            return areAccountNumbersPresent;
        }
    }
}

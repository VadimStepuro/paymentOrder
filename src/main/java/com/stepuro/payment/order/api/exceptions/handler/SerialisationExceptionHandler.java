package com.stepuro.payment.order.api.exceptions.handler;

import com.stepuro.payment.order.api.dto.ApiError;
import org.hibernate.type.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SerialisationExceptionHandler{

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SerializationException.class)
    public ApiError handleValidationExceptions(SerializationException ex){
        return new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
}

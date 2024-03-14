package com.stepuro.payment.order.api.exceptions.handler;

import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.exceptions.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServerExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException.class)
    public ApiError handleServerException(ServerException ex){
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }
}

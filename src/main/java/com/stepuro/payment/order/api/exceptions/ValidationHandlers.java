package com.stepuro.payment.order.api.exceptions;

import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.ApiSubError;
import com.stepuro.payment.order.api.dto.ApiValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class ValidationHandlers {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiSubError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            String objectName = error.getObjectName();

            errors.add(new ApiValidationError(objectName, fieldName, rejectedValue, errorMessage));
        });

        return new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex, errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SQLException.class)
    public ApiError handleSqlExceptions(SQLException ex){
        return new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClientException.class)
    public ApiError handleClientException(ClientException ex){
        return new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
}
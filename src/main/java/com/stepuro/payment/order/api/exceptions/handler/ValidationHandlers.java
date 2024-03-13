package com.stepuro.payment.order.api.exceptions.handler;

import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.ApiSubError;
import com.stepuro.payment.order.api.dto.ApiValidationError;
import com.stepuro.payment.order.api.exceptions.ClientException;
import org.springframework.http.HttpStatus;
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
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            Object rejectedValue = error.getRejectedValue();
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            String objectName = error.getObjectName();

            errors.add(new ApiValidationError(objectName, fieldName, rejectedValue, errorMessage));
        });

        StringBuilder returnMessage = new StringBuilder();
        ex.getAllErrors().forEach((error) -> {
            returnMessage.append(error.getDefaultMessage());
            returnMessage.append(" (");
            returnMessage.append(error.getObjectName());
            returnMessage.append(") ");
        });

        return new ApiError(HttpStatus.BAD_REQUEST, returnMessage.toString(), ex, errors);
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
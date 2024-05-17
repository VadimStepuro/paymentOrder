package com.stepuro.payment.order.controller;

import com.stepuro.payment.order.api.annotation.Loggable;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.service.TransferEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TransferEntityController {
    private final TransferEntityService transferEntityService;

    public TransferEntityController(TransferEntityService transferEntityService) {
        this.transferEntityService = transferEntityService;
    }

    @Operation(summary = "Create payment order entity with create payment request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created payment order entity",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentOrderEntityDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid create payment request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    @Loggable
    @PostMapping(value = "payment_order_entities/transfer_amount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentOrderEntityDto> transferAmount(@RequestBody @Valid PaymentOrderEntityDto paymentOrderEntityDto){
        PaymentOrderEntityDto payment = transferEntityService.createPayment(paymentOrderEntityDto);

        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @Operation(summary = "Create payment order entity with create payment request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created payment order entity",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentOrderEntityDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid create payment request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    @Loggable
    @PostMapping(value = "payment_order_entities/transfer_amount_rest_client", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentOrderEntityDto> transferAmountRestClient(@RequestBody @Valid PaymentOrderEntityDto paymentOrderEntityDto){
        PaymentOrderEntityDto payment = transferEntityService.createRestClientPayment(paymentOrderEntityDto);

        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

}

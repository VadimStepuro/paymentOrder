package com.stepuro.payment.order.controller;

import com.stepuro.payment.order.api.annotation.Loggable;
import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.service.PaymentOrderEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PaymentOrderEntityController {
    @Autowired
    private PaymentOrderEntityService paymentOrderEntityService;

    @Operation(summary = "Get all payment order entities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All found payment order entities",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PaymentOrderEntityDto.class)))}),
            @ApiResponse(responseCode = "204", description = "No payment order entity found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })

    @Loggable
    @GetMapping(value = "/payment_order_entities", produces = "application/json")
    public ResponseEntity<List<PaymentOrderEntityDto>> findAll(){
        List<PaymentOrderEntityDto> allPaymentOrderEntities = paymentOrderEntityService.findAll();

        return new ResponseEntity<>(allPaymentOrderEntities, HttpStatus.OK);
    }

    @Operation(summary = "Get payment order entity by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found payment order entity by id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentOrderEntityDto.class))}),
            @ApiResponse(responseCode = "404", description = "Payment order entity not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))})
    @Loggable
    @GetMapping(value = "/payment_order_entities/{id}", produces = "application/json")
    public ResponseEntity<PaymentOrderEntityDto> findById(@PathVariable("id") UUID id){
        PaymentOrderEntityDto foundPaymentOrderEntity = paymentOrderEntityService.findById(id);

        return new ResponseEntity<>(foundPaymentOrderEntity, HttpStatus.OK);
    }



    @Operation(summary = "Create payment order entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created payment order entity",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentOrderEntityDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid payment order entity",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HashMap.class)))})
    @Loggable
    @PostMapping(value = "/payment_order_entities", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentOrderEntityDto> create(@RequestBody @Valid PaymentOrderEntityDto paymentOrderEntityDto){
        PaymentOrderEntityDto createdPaymentOrderEntity = paymentOrderEntityService.create(paymentOrderEntityDto);

        return new ResponseEntity<>(createdPaymentOrderEntity, HttpStatus.CREATED);
    }

    @Operation(summary = "Edit payment order entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edited payment order entity",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentOrderEntityDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid payment order entity",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HashMap.class))),
            @ApiResponse(responseCode = "404", description = "Payment order entity not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))})
    @Loggable
    @PutMapping(value = "/payment_order_entities", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentOrderEntityDto> edit(@RequestBody @Valid PaymentOrderEntityDto paymentOrderEntityDto){
        PaymentOrderEntityDto editedPaymentOrderEntity = paymentOrderEntityService.edit(paymentOrderEntityDto);

        return new ResponseEntity<>(editedPaymentOrderEntity, HttpStatus.OK);
    }

    @Operation(summary = "Delete payment order entity by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deletes payment order entity by id",
                    content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Payment order entity not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    @Loggable
    @DeleteMapping(value = "/payment_order_entities/{id}", produces = "application/json")
    public void delete(@PathVariable("id") UUID id){
        paymentOrderEntityService.delete(id);
    }
}

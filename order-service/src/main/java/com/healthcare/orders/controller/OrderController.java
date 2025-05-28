package com.healthcare.orders.controller;

import com.healthcare.orders.dto.OrderRequest;
import com.healthcare.orders.dto.OrderResponse;
import com.healthcare.orders.exception.OrderNotFoundException;
import com.healthcare.orders.model.Order;
import com.healthcare.orders.service.OrderSagaOrchestrator;
import com.healthcare.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create a new order", description = "Creates a new order with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {

        logger.info("Received request to create order for user: {}", request.getUserId());

        return orderService.createOrderAsync(request)
                .thenApply(order -> {
                    logger.info("Successfully created order with ID: {}", order.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(order);
                })
                .exceptionally(ex -> {
                    logger.error("Error creating order", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    //use saga orchestrator
    @Autowired
    private OrderSagaOrchestrator sagaOrchestrator;

    @PostMapping
    public void createOrder(@RequestBody Order order) {
        logger.info("Creating order: {}", order);

        sagaOrchestrator.createOrderSaga(order);
    }


    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<OrderResponse>> getOrderById(
            @PathVariable Long id) {

        logger.debug("Fetching order with ID: {}", id);

        return orderService.getOrderByIdAsync(id)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof OrderNotFoundException) {
                        return ResponseEntity.<OrderResponse>notFound().build();
                    }
                    logger.error("Error fetching order with ID: {}", id, ex);
                    return ResponseEntity.<OrderResponse>status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Operation(summary = "Cancel an order", description = "Cancels the order with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order status"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/cancel")
    public CompletableFuture<ResponseEntity<Void>> cancelOrder(
            @PathVariable Long id) {

        logger.info("Request to cancel order with ID: {}", id);

        return orderService.cancelOrderAsync(id)
                .thenApply(voidResult -> ResponseEntity.ok().<Void>build())
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest().build();
                    } else if (ex.getCause() instanceof OrderNotFoundException) {
                        return ResponseEntity.notFound().build();
                    } else {
                        logger.error("Error cancelling order with ID: {}", id, ex);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                });
    }
}
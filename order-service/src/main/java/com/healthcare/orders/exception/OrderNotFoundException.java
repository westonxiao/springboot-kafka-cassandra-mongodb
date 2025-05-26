package com.healthcare.orders.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an order with the specified ID cannot be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message explaining which order wasn't found
     */
    public OrderNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message explaining which order wasn't found
     * @param cause the underlying cause of this exception
     */
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory method to create an exception for a specific order ID.
     * @param orderId the ID of the order that wasn't found
     * @return a new OrderNotFoundException instance
     */
    public static OrderNotFoundException forOrderId(Long orderId) {
        return new OrderNotFoundException("Order not found with ID: " + orderId);
    }

    /**
     * Factory method to create an exception for a specific user's order.
     * @param userId the user ID whose order wasn't found
     * @param orderId the ID of the order that wasn't found
     * @return a new OrderNotFoundException instance
     */
    public static OrderNotFoundException forUserOrder(String userId, Long orderId) {
        return new OrderNotFoundException(
                String.format("Order with ID %s not found for user %s", orderId, userId));
    }
}
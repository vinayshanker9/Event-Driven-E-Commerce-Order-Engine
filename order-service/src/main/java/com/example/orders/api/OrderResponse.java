package com.example.orders.api;

import com.example.orders.domain.OrderEntity;
import com.example.orders.domain.OrderStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderResponse(UUID id, OrderStatus status, BigDecimal totalAmount, String currency, String rejectionReason) {
    public static OrderResponse from(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getRejectionReason()
        );
    }
}

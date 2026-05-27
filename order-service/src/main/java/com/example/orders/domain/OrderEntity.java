package com.example.orders.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal totalAmount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String rejectionReason;
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderLineEmbeddable> lines = new ArrayList<>();

    protected OrderEntity() {
    }

    public OrderEntity(UUID id, UUID customerId, BigDecimal totalAmount, String currency, List<OrderLineEmbeddable> lines) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.lines = new ArrayList<>(lines);
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderLineEmbeddable> getLines() {
        return lines;
    }

    public void markInventoryReserved() {
        this.status = OrderStatus.INVENTORY_RESERVED;
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        this.status = OrderStatus.REJECTED;
        this.rejectionReason = reason;
    }
}

package com.example.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    private UUID id;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
    private String currency;
    private String reference;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String rejectionReason;
    private Instant createdAt;

    protected PaymentEntity() {
    }

    public PaymentEntity(UUID orderId, UUID customerId, BigDecimal amount, String currency, PaymentStatus status, String reference, String rejectionReason) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.reference = reference;
        this.rejectionReason = rejectionReason;
        this.createdAt = Instant.now();
    }
}

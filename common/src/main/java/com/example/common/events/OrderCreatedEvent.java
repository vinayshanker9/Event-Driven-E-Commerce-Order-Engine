package com.example.common.events;

import com.example.common.model.Money;
import com.example.common.model.OrderLine;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        List<OrderLine> lines,
        Money total,
        Instant occurredAt
) {
}

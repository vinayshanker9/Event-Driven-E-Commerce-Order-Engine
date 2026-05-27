package com.example.common.events;

import java.util.UUID;

public record PaymentRejectedEvent(UUID orderId, String reason) {
}

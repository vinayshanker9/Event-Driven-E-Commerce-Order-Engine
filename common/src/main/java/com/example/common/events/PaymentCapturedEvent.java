package com.example.common.events;

import java.util.UUID;

public record PaymentCapturedEvent(UUID orderId, String paymentReference) {
}

package com.example.common.events;

import com.example.common.model.Money;
import java.util.UUID;

public record CapturePaymentCommand(UUID orderId, UUID customerId, Money amount) {
}

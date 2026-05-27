package com.example.common.events;

import java.util.UUID;

public record InventoryRejectedEvent(UUID orderId, String reason) {
}

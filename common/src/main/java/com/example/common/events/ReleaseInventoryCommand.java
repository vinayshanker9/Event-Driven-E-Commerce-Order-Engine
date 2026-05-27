package com.example.common.events;

import com.example.common.model.OrderLine;
import java.util.List;
import java.util.UUID;

public record ReleaseInventoryCommand(UUID orderId, List<OrderLine> lines) {
}

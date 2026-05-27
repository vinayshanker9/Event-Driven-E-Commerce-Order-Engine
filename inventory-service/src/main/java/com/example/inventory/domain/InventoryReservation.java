package com.example.inventory.domain;

import com.example.common.model.OrderLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("inventory_reservations")
public class InventoryReservation {
    @Id
    private UUID orderId;
    private List<OrderLine> lines;

    protected InventoryReservation() {
    }

    public InventoryReservation(UUID orderId, List<OrderLine> lines) {
        this.orderId = orderId;
        this.lines = List.copyOf(lines);
    }

    public UUID getOrderId() {
        return orderId;
    }

    public List<OrderLine> getLines() {
        return lines;
    }
}

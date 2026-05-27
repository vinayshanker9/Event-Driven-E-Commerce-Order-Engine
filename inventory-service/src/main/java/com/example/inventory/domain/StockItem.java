package com.example.inventory.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("stock_items")
public class StockItem {
    @Id
    private String sku;
    private int available;
    private int reserved;

    protected StockItem() {
    }

    public StockItem(String sku, int available) {
        this.sku = sku;
        this.available = available;
    }

    public String getSku() {
        return sku;
    }

    public int getAvailable() {
        return available;
    }

    public int getReserved() {
        return reserved;
    }

    public boolean canReserve(int quantity) {
        return available >= quantity;
    }

    public void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new IllegalStateException("not enough stock for " + sku);
        }
        available -= quantity;
        reserved += quantity;
    }

    public void release(int quantity) {
        reserved = Math.max(0, reserved - quantity);
        available += quantity;
    }
}

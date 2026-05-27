package com.example.orders.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderLineEmbeddable {
    private String sku;
    private int quantity;

    protected OrderLineEmbeddable() {
    }

    public OrderLineEmbeddable(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }
}

package com.example.inventory.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRepository extends MongoRepository<StockItem, String> {
}

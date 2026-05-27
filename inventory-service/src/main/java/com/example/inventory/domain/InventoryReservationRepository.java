package com.example.inventory.domain;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryReservationRepository extends MongoRepository<InventoryReservation, UUID> {
}

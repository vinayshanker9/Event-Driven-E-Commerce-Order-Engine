package com.example.inventory.events;

import com.example.common.events.InventoryRejectedEvent;
import com.example.common.events.InventoryReservedEvent;
import com.example.common.events.ReleaseInventoryCommand;
import com.example.common.events.ReserveInventoryCommand;
import com.example.common.events.Topics;
import com.example.inventory.domain.InventoryReservation;
import com.example.inventory.domain.InventoryReservationRepository;
import com.example.inventory.domain.StockRepository;
import java.util.ArrayList;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryCommandHandler {
    private final StockRepository repository;
    private final InventoryReservationRepository reservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryCommandHandler(
            StockRepository repository,
            InventoryReservationRepository reservationRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.repository = repository;
        this.reservationRepository = reservationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = Topics.INVENTORY_COMMANDS, groupId = "inventory-service")
    public void handle(Object message) {
        Object command = message;
        if (message instanceof org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record) {
            command = record.value();
        }
        System.out.println("DEBUG: InventoryCommandHandler received command of type: " + (command != null ? command.getClass().getName() : "null") + ", value: " + command);
        if (command instanceof ReserveInventoryCommand reserve) {
            reserveInventory(reserve);
        } else if (command instanceof ReleaseInventoryCommand release) {
            releaseInventory(release);
        }
    }

    private void reserveInventory(ReserveInventoryCommand command) {
        if (reservationRepository.existsById(command.orderId())) {
            kafkaTemplate.send(Topics.INVENTORY_REPLIES, command.orderId().toString(),
                    new InventoryReservedEvent(command.orderId()));
            return;
        }

        var items = new ArrayList<>(repository.findAllById(command.lines().stream().map(line -> line.sku()).toList()));
        var bySku = items.stream().collect(java.util.stream.Collectors.toMap(item -> item.getSku(), item -> item));

        for (var line : command.lines()) {
            var item = bySku.get(line.sku());
            if (item == null || !item.canReserve(line.quantity())) {
                kafkaTemplate.send(Topics.INVENTORY_REPLIES, command.orderId().toString(),
                        new InventoryRejectedEvent(command.orderId(), "insufficient stock for sku " + line.sku()));
                return;
            }
        }

        command.lines().forEach(line -> bySku.get(line.sku()).reserve(line.quantity()));
        repository.saveAll(items);
        reservationRepository.save(new InventoryReservation(command.orderId(), command.lines()));
        kafkaTemplate.send(Topics.INVENTORY_REPLIES, command.orderId().toString(),
                new InventoryReservedEvent(command.orderId()));
    }

    private void releaseInventory(ReleaseInventoryCommand command) {
        if (!reservationRepository.existsById(command.orderId())) {
            return;
        }

        var items = new ArrayList<>(repository.findAllById(command.lines().stream().map(line -> line.sku()).toList()));
        var bySku = items.stream().collect(java.util.stream.Collectors.toMap(item -> item.getSku(), item -> item));
        command.lines().forEach(line -> {
            var item = bySku.get(line.sku());
            if (item != null) {
                item.release(line.quantity());
            }
        });
        repository.saveAll(items);
        reservationRepository.deleteById(command.orderId());
    }
}

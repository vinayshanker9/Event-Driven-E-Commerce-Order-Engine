package com.example.orders.events;

import com.example.common.events.CapturePaymentCommand;
import com.example.common.events.InventoryRejectedEvent;
import com.example.common.events.InventoryReservedEvent;
import com.example.common.events.OrderCreatedEvent;
import com.example.common.events.PaymentCapturedEvent;
import com.example.common.events.PaymentRejectedEvent;
import com.example.common.events.ReleaseInventoryCommand;
import com.example.common.events.ReserveInventoryCommand;
import com.example.common.events.Topics;
import com.example.common.model.Money;
import com.example.common.model.OrderLine;
import com.example.orders.api.CreateOrderRequest;
import com.example.orders.domain.OrderEntity;
import com.example.orders.domain.OrderLineEmbeddable;
import com.example.orders.domain.OrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderSagaService {
    private final OrderRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderSagaService(OrderRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {
        var orderId = UUID.randomUUID();
        var lines = request.lines().stream()
                .map(line -> new OrderLineEmbeddable(line.sku(), line.quantity()))
                .toList();
        var order = repository.save(new OrderEntity(
                orderId,
                request.customerId(),
                request.total().amount(),
                request.total().currency(),
                lines
        ));

        kafkaTemplate.send(Topics.ORDER_EVENTS, orderId.toString(),
                new OrderCreatedEvent(orderId, request.customerId(), request.lines(), request.total(), Instant.now()));
        kafkaTemplate.send(Topics.INVENTORY_COMMANDS, orderId.toString(),
                new ReserveInventoryCommand(orderId, request.lines()));
        return order;
    }

    @KafkaListener(topics = Topics.INVENTORY_REPLIES, groupId = "order-service")
    @Transactional
    public void onInventoryReply(Object message) {
        Object reply = message;
        if (message instanceof org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record) {
            reply = record.value();
        }
        System.out.println("DEBUG: OrderSagaService onInventoryReply received type: " + (reply != null ? reply.getClass().getName() : "null") + ", value: " + reply);
        if (reply instanceof InventoryReservedEvent event) {
            repository.findById(event.orderId()).ifPresent(order -> {
                order.markInventoryReserved();
                kafkaTemplate.send(Topics.PAYMENT_COMMANDS, event.orderId().toString(),
                        new CapturePaymentCommand(event.orderId(), order.getCustomerId(),
                                new Money(order.getTotalAmount(), order.getCurrency())));
            });
        } else if (reply instanceof InventoryRejectedEvent event) {
            repository.findById(event.orderId()).ifPresent(order -> order.reject(event.reason()));
        }
    }

    @KafkaListener(topics = Topics.PAYMENT_REPLIES, groupId = "order-service")
    @Transactional
    public void onPaymentReply(Object message) {
        Object reply = message;
        if (message instanceof org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record) {
            reply = record.value();
        }
        System.out.println("DEBUG: OrderSagaService onPaymentReply received type: " + (reply != null ? reply.getClass().getName() : "null") + ", value: " + reply);
        if (reply instanceof PaymentCapturedEvent event) {
            repository.findById(event.orderId()).ifPresent(OrderEntity::confirm);
        } else if (reply instanceof PaymentRejectedEvent event) {
            repository.findById(event.orderId()).ifPresent(order -> {
                order.reject(event.reason());
                kafkaTemplate.send(Topics.INVENTORY_COMMANDS, event.orderId().toString(),
                        new ReleaseInventoryCommand(event.orderId(), toCommonLines(order.getLines())));
            });
        }
    }

    private List<OrderLine> toCommonLines(List<OrderLineEmbeddable> lines) {
        return lines.stream()
                .map(line -> new OrderLine(line.getSku(), line.getQuantity()))
                .toList();
    }
}

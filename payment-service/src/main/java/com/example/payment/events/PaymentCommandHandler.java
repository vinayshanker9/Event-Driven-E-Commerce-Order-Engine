package com.example.payment.events;

import com.example.common.events.CapturePaymentCommand;
import com.example.common.events.PaymentCapturedEvent;
import com.example.common.events.PaymentRejectedEvent;
import com.example.common.events.Topics;
import com.example.payment.domain.PaymentEntity;
import com.example.payment.domain.PaymentRepository;
import com.example.payment.domain.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentCommandHandler {
    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentCommandHandler(PaymentRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = Topics.PAYMENT_COMMANDS, groupId = "payment-service")
    public void handle(CapturePaymentCommand command) {
        repository.findByOrderId(command.orderId()).ifPresentOrElse(
                payment -> kafkaTemplate.send(Topics.PAYMENT_REPLIES, command.orderId().toString(),
                        new PaymentCapturedEvent(command.orderId(), "duplicate-" + command.orderId())),
                () -> capture(command)
        );
    }

    private void capture(CapturePaymentCommand command) {
        if (command.amount().amount().compareTo(BigDecimal.valueOf(10000)) > 0) {
            repository.save(new PaymentEntity(command.orderId(), command.customerId(), command.amount().amount(),
                    command.amount().currency(), PaymentStatus.REJECTED, null, "payment limit exceeded"));
            kafkaTemplate.send(Topics.PAYMENT_REPLIES, command.orderId().toString(),
                    new PaymentRejectedEvent(command.orderId(), "payment limit exceeded"));
            return;
        }

        var reference = "pay_" + UUID.randomUUID();
        repository.save(new PaymentEntity(command.orderId(), command.customerId(), command.amount().amount(),
                command.amount().currency(), PaymentStatus.CAPTURED, reference, null));
        kafkaTemplate.send(Topics.PAYMENT_REPLIES, command.orderId().toString(),
                new PaymentCapturedEvent(command.orderId(), reference));
    }
}

package dev.sorokin.auditsystem.kafka;

import dev.sorokin.auditsystem.domain.AuditService;
import dev.sorokin.common.events.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);
    private final AuditService auditService;

    public PaymentEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.payments}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(PaymentEvent event) {
        logger.info("Received event: paymentId={}, eventType={}", event.paymentId(), event.type());
        auditService.processEvent(event);
    }

}

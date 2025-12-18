package dev.sorokin.auditsystem.domain;

import dev.sorokin.auditsystem.api.PaymentAuditDto;
import dev.sorokin.auditsystem.domain.db.PaymentAuditLogEntity;
import dev.sorokin.auditsystem.domain.db.PaymentAuditLogRepository;
import dev.sorokin.common.events.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final PaymentAuditLogRepository auditLogRepository;

    public AuditService(PaymentAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void processEvent(PaymentEvent event) {
        // TODO: проверить на дубликаты
        PaymentAuditLogEntity logEntry = convertEventToLogEntity(event);
        auditLogRepository.save(logEntry);
        logger.info("Stored audit log for payment {}", event.paymentId());
    }

    @Transactional(readOnly = true)
    public List<PaymentAuditDto> getByPaymentId(Long paymentId) {
        return auditLogRepository.findByPaymentId(paymentId).stream()
                .map(this::convertToPaymentAuditDto)
                .toList();
    }

    private PaymentAuditDto convertToPaymentAuditDto(PaymentAuditLogEntity entry) {
        return new PaymentAuditDto(
                entry.getEventId(),
                entry.getPaymentId(),
                entry.getEventType(),
                entry.getOccurredAt(),
                entry.getAmount(),
                entry.getUserId(),
                entry.getStatus(),
                entry.getCreatedAt()
        );
    }

    private PaymentAuditLogEntity convertEventToLogEntity(PaymentEvent event) {
        return new PaymentAuditLogEntity(
                event.eventId(),
                event.paymentId(),
                event.type().name(),
                event.occurredAt(),
                event.amount(),
                event.userId(),
                event.status()
        );
    }
}

package dev.sorokin.paymentsystem.domain;

import dev.sorokin.paymentsystem.api.dto.CreatePaymentRequest;
import dev.sorokin.paymentsystem.api.dto.PaymentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentDto createPayment(CreatePaymentRequest request) {
        // TODO: валидация суммы
        PaymentEntity payment = new PaymentEntity(request.userId(), request.amount(), PaymentStatus.NEW);
        PaymentEntity saved = paymentRepository.save(payment);
        log.info("Payment created: id={}", saved.getId());
        return convertEntityToDto(saved);
    }

    public PaymentDto getPayment(Long id) {
        PaymentEntity payment = findPaymentOrThrow(id);
        return convertEntityToDto(payment);
    }

    @Transactional
    public PaymentDto confirmPayment(Long id) {
        PaymentEntity payment = findPaymentOrThrow(id);
        // TODO: проверка статуса NEW
        payment.setStatus(PaymentStatus.SUCCEEDED);
        PaymentEntity saved = paymentRepository.save(payment);
        log.info("Payment has been confirmed: id={}", id);
        return convertEntityToDto(saved);
    }

    private PaymentEntity findPaymentOrThrow(Long id) {
        // TODO: выкидывать 404 ошибку
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment with id=" + id + " not found"));
    }

    private PaymentDto convertEntityToDto(PaymentEntity payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}

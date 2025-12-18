package dev.sorokin.paymentsystem.domain;

import dev.sorokin.paymentsystem.api.dto.CreatePaymentRequest;
import dev.sorokin.paymentsystem.api.dto.PaymentDto;
import dev.sorokin.paymentsystem.domain.db.PaymentEntity;
import dev.sorokin.paymentsystem.domain.db.PaymentRepository;
import dev.sorokin.paymentsystem.domain.db.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");

    private final PaymentEntityMapper mapper;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, PaymentDto> redisTemplate;

    public PaymentService(
            PaymentEntityMapper mapper,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            RedisTemplate<String, PaymentDto> redisTemplate
    ) {
        this.mapper = mapper;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public PaymentDto createPayment(CreatePaymentRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found id = " + request.userId());
        }

        if (request.amount().compareTo(MAX_AMOUNT) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount too large");
        }
        PaymentEntity payment = new PaymentEntity(request.userId(), request.amount(), PaymentStatus.NEW);
        PaymentEntity saved = paymentRepository.save(payment);
        log.info("Payment created: id={}", saved.getId());

        return mapper.convertEntityToDto(saved);
    }

    @Cacheable(cacheNames = "payments", key = "#id")
    public PaymentDto getPayment(Long id) {
        PaymentEntity payment = findPaymentOrThrow(id);
        return mapper.convertEntityToDto(payment);
    }

    @CacheEvict(cacheNames = "payments", key = "#id")
    @Transactional
    public PaymentDto confirmPayment(Long id) {
        PaymentEntity payment = findPaymentOrThrow(id);
        if (!payment.getStatus().equals(PaymentStatus.NEW)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment status must be NEW");
        }
        payment.setStatus(PaymentStatus.SUCCEEDED);
        PaymentEntity saved = paymentRepository.save(payment);
        log.info("Payment has been confirmed: id={}", id);
        return mapper.convertEntityToDto(saved);
    }

//    public PaymentDto getPayment(Long id) {
//        var foundInCache = redisTemplate.opsForValue().get(id.toString());
//        if (foundInCache != null) {
//            log.info("Payment found in cache: id={}", foundInCache.id());
//            return foundInCache;
//        }
//        PaymentEntity payment = findPaymentOrThrow(id);
//        var paymentDto = mapper.convertEntityToDto(payment);
//        redisTemplate.opsForValue().set(id.toString(), paymentDto);
//        return paymentDto;
//    }
//
//    @Transactional
//    public PaymentDto confirmPayment(Long id) {
//        PaymentEntity payment = findPaymentOrThrow(id);
//        if (!payment.getStatus().equals(PaymentStatus.NEW)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment status must be NEW");
//        }
//        payment.setStatus(PaymentStatus.SUCCEEDED);
//        PaymentEntity saved = paymentRepository.save(payment);
//        log.info("Payment has been confirmed: id={}", id);
//        redisTemplate.delete(id.toString());
//        log.info("Payment has been removed from cache: id={}", id);
//        return mapper.convertEntityToDto(saved);
//    }

    private PaymentEntity findPaymentOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment with id=" + id + " not found"));
    }
}

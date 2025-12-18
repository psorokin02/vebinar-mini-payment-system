package dev.sorokin.paymentsystem.api;

import dev.sorokin.paymentsystem.api.dto.CreatePaymentRequest;
import dev.sorokin.paymentsystem.api.dto.PaymentDto;
import dev.sorokin.paymentsystem.domain.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentDto create(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/{id}")
    public PaymentDto get(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }

    @PostMapping("/{id}/confirm")
    public PaymentDto confirm(@PathVariable Long id) {
        return paymentService.confirmPayment(id);
    }
}

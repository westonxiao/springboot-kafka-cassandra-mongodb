package com.healthcare.payments.controller;

import com.healthcare.payments.model.Payment;
import com.healthcare.payments.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

/*    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACCOUNTANT')")
    public List<Payment> getAllPayments() {
        //return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('ACCOUNTANT')")
    public Payment getPaymentById(@PathVariable String id) {
        //return paymentService.getPaymentById(id);
    }*/

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Payment createPayment(@RequestBody Payment payment) {
       // return paymentService.createPayment(payment);
        return payment;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePayment(@PathVariable String id) {
        //paymentService.deletePayment(id);
    }
}
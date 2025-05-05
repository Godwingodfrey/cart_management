package com.example.coffeeAPI.controller;

import com.example.coffeeAPI.dto.PaymentRequest;
import com.example.coffeeAPI.dto.PaymentResponse;
import com.example.coffeeAPI.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@PreAuthorize("hasRole('USER')")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.initiatePayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestParam String paymentId) {
        String result = paymentService.verifyPayment(paymentId);
        return ResponseEntity.ok(result);
    }
}

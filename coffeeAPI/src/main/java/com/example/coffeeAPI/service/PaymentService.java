package com.example.coffeeAPI.service;

import com.example.coffeeAPI.dto.PaymentRequest;
import com.example.coffeeAPI.dto.PaymentResponse;
import com.example.coffeeAPI.exception.ResourceNotFoundException;
import com.example.coffeeAPI.model.Order;
import com.example.coffeeAPI.model.Payment;
import com.example.coffeeAPI.repository.OrderRepository;
import com.example.coffeeAPI.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + paymentRequest.getOrderId()));

        if (!order.getStatus().equals(Order.OrderStatus.PENDING)) {
            throw new IllegalStateException("Order is not in a payable state");
        }

        String paymentId = "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentId);
        response.setOrderId(order.getId());
        response.setAmount(order.getTotalAmount());
        response.setStatus("PENDING");
        response.setMessage("Payment initiated successfully. Proceed with verification.");

        return response;
    }

    public String verifyPayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (!payment.getStatus().equals("PENDING")) {
            return "Payment already processed.";
        }

        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(Order.OrderStatus.COMPLETED);
        orderRepository.save(order);

        return "Payment verified. Order marked as COMPLETED.";
    }
}

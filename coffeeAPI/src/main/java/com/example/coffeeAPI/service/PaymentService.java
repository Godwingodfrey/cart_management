package com.example.coffeeAPI.service;

import com.example.coffeeAPI.dto.PaymentRequest;
import com.example.coffeeAPI.dto.PaymentResponse;
import com.example.coffeeAPI.exception.ResourceNotFoundException;
import com.example.coffeeAPI.model.Order;
import com.example.coffeeAPI.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + paymentRequest.getOrderId()));

        if (!order.getStatus().equals(Order.OrderStatus.PENDING)) {
            throw new IllegalStateException("Order is not in a payable state");
        }

        String paymentId = "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);


        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentId);
        response.setOrderId(order.getId());
        response.setAmount(order.getTotalAmount());
        response.setStatus("PENDING");
        response.setMessage("Payment initiated successfully. Proceed with verification.");

        return response;
    }

    public String verifyPayment(String paymentId) {
        return "Payment verified successfully. Status: COMPLETED";
    }
}

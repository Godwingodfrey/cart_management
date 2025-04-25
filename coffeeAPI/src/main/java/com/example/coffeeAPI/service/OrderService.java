package com.example.coffeeAPI.service;

import com.example.coffeeAPI.dto.OrderDto;
import com.example.coffeeAPI.dto.OrderItemDto;
import com.example.coffeeAPI.exception.ResourceNotFoundException;
import com.example.coffeeAPI.model.*;
import com.example.coffeeAPI.repository.CoffeeRepository;
import com.example.coffeeAPI.repository.OrderRepository;
import com.example.coffeeAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    public List<OrderDto> getUserOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return orderRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!order.getUser().getUsername().equals(username) &&
                !SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

        return convertToDto(order);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Long createOrderFromCart(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Coffee coffee = cartItem.getCoffee();

            if (coffee.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for coffee: " + coffee.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCoffee(coffee);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(coffee.getPrice());

            order.getItems().add(orderItem);

            total = total.add(coffee.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            coffee.setStockQuantity(coffee.getStockQuantity() - cartItem.getQuantity());
            coffeeRepository.save(coffee);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    public OrderDto updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            return convertToDto(orderRepository.save(order));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    private OrderDto convertToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setUsername(order.getUser().getUsername());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setStatus(order.getStatus().name());
        orderDto.setTotalAmount(order.getTotalAmount());

        order.getItems().forEach(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setCoffeeId(item.getCoffee().getId());
            itemDto.setCoffeeName(item.getCoffee().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            orderDto.getItems().add(itemDto);
        });

        return orderDto;
    }
}
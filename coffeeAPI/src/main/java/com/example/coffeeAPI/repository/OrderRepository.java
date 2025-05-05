package com.example.coffeeAPI.repository;

import com.example.coffeeAPI.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserId(Long userId);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
}

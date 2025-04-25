package com.example.coffeeAPI.controller;

import com.example.coffeeAPI.dto.CartDto;
import com.example.coffeeAPI.dto.CartItemDto;
import com.example.coffeeAPI.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('USER')")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addToCart(@RequestBody CartItemDto cartItemDto) {
        return ResponseEntity.ok(cartService.addToCart(cartItemDto));
    }

    @PostMapping("/remove")
    public ResponseEntity<CartDto> removeFromCart(@RequestBody CartItemDto cartItemDto) {
        return ResponseEntity.ok(cartService.removeFromCart(cartItemDto));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Long> checkout() {
        return ResponseEntity.ok(cartService.checkout());
    }
}

package com.example.coffeeAPI.service;

import com.example.coffeeAPI.dto.CartDto;
import com.example.coffeeAPI.dto.CartItemDto;
import com.example.coffeeAPI.exception.ResourceNotFoundException;
import com.example.coffeeAPI.model.Cart;
import com.example.coffeeAPI.model.CartItem;
import com.example.coffeeAPI.model.Coffee;
import com.example.coffeeAPI.model.User;
import com.example.coffeeAPI.repository.CartRepository;
import com.example.coffeeAPI.repository.CoffeeRepository;
import com.example.coffeeAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    public void createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
    }

    public CartDto getCart() {
        Cart cart = getCurrentUserCart();
        return convertToDto(cart);
    }

    public CartDto addToCart(CartItemDto cartItemDto) {
        log.info("Adding to cart: {}", cartItemDto);

        if (cartItemDto.getCoffeeId() == null) {
            log.error("Coffee ID is null");
            throw new IllegalArgumentException("Coffee ID must not be null");
        }
        if (cartItemDto.getQuantity() == null || cartItemDto.getQuantity() < 1) {
            log.error("Invalid quantity: {}", cartItemDto.getQuantity());
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = getCurrentUserCart();
        log.debug("Fetched cart for user: {}", cart.getUser().getUsername());

        Coffee coffee = coffeeRepository.findById(cartItemDto.getCoffeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found with id: " + cartItemDto.getCoffeeId()));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getCoffee().getId().equals(coffee.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItemDto.getQuantity());
            log.debug("Updated quantity for coffee {}: {}", coffee.getId(), item.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCoffee(coffee);
            newItem.setQuantity(cartItemDto.getQuantity());
            cart.addItem(newItem);
            log.debug("Added new item to cart: coffeeId={}, quantity={}", coffee.getId(), cartItemDto.getQuantity());
        }

        cartRepository.save(cart);
        log.info("Cart saved successfully for user: {}", cart.getUser().getUsername());
        return convertToDto(cart);
    }

    public CartDto removeFromCart(CartItemDto cartItemDto) {
        Cart cart = getCurrentUserCart();
        Coffee coffee = coffeeRepository.findById(cartItemDto.getCoffeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found with id: " + cartItemDto.getCoffeeId()));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getCoffee().getId().equals(coffee.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (item.getQuantity() > cartItemDto.getQuantity()) {
                item.setQuantity(item.getQuantity() - cartItemDto.getQuantity());
            } else {
                cart.removeItem(item);
            }
        }

        cartRepository.save(cart);
        return convertToDto(cart);
    }

    public Long checkout() {
        Cart cart = getCurrentUserCart();
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        Long orderId = orderService.createOrderFromCart(cart);

        cart.getItems().clear();
        cartRepository.save(cart);

        return orderId;
    }

    private Cart getCurrentUserCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));
    }

    private CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cart.getItems().forEach(item -> {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setCoffeeId(item.getCoffee().getId());
            itemDto.setCoffeeName(item.getCoffee().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getCoffee().getPrice());
            cartDto.getItems().add(itemDto);
        });

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getCoffee().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartDto.setTotal(total);
        return cartDto;
    }
}

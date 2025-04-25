package com.example.coffeeAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CartItemDto {
    private Long coffeeId;
    private String coffeeName;
    private Integer quantity;
    private BigDecimal price;
}

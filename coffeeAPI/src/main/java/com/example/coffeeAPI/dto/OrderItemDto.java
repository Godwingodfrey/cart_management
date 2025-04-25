package com.example.coffeeAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long coffeeId;
    private String coffeeName;
    private Integer quantity;
    private BigDecimal price;
}

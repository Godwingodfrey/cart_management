package com.example.coffeeAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CartDto {
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal total;
}

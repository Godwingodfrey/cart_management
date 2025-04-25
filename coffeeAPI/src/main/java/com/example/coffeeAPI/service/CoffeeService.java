package com.example.coffeeAPI.service;

import com.example.coffeeAPI.dto.CoffeeDto;
import com.example.coffeeAPI.exception.ResourceNotFoundException;
import com.example.coffeeAPI.model.Coffee;
import com.example.coffeeAPI.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }

    public Coffee getCoffeeById(Long id) {
        return coffeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found with id: " + id));
    }

    public Coffee addCoffee(CoffeeDto coffeeDto) {
        Coffee coffee = new Coffee();
        coffee.setName(coffeeDto.getName());
        coffee.setDescription(coffeeDto.getDescription());
        coffee.setPrice(coffeeDto.getPrice());
        coffee.setStockQuantity(coffeeDto.getStockQuantity());
        coffee.setImageUrl(coffeeDto.getImageUrl());
        return coffeeRepository.save(coffee);
    }

    public Coffee updateCoffee(Long id, CoffeeDto coffeeDto) {
        Coffee coffee = getCoffeeById(id);
        coffee.setName(coffeeDto.getName());
        coffee.setDescription(coffeeDto.getDescription());
        coffee.setPrice(coffeeDto.getPrice());
        coffee.setStockQuantity(coffeeDto.getStockQuantity());
        coffee.setImageUrl(coffeeDto.getImageUrl());
        return coffeeRepository.save(coffee);
    }

    public void deleteCoffee(Long id) {
        Coffee coffee = getCoffeeById(id);
        coffeeRepository.delete(coffee);
    }
}

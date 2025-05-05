package com.example.coffeeAPI.controller;

import com.example.coffeeAPI.dto.CoffeeDto;
import com.example.coffeeAPI.model.Coffee;
import com.example.coffeeAPI.service.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coffees")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    @GetMapping
    public ResponseEntity<List<Coffee>> getAllCoffees() {
        return ResponseEntity.ok(coffeeService.getAllCoffees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getCoffeeById(@PathVariable Long id) {
        return ResponseEntity.ok(coffeeService.getCoffeeById(id));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coffee> addCoffee(@RequestBody CoffeeDto coffeeDto) {
        return ResponseEntity.ok(coffeeService.addCoffee(coffeeDto));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coffee> updateCoffee(@PathVariable Long id, @RequestBody CoffeeDto coffeeDto) {
        return ResponseEntity.ok(coffeeService.updateCoffee(id, coffeeDto));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        coffeeService.deleteCoffee(id);
        return ResponseEntity.noContent().build();
    }
}
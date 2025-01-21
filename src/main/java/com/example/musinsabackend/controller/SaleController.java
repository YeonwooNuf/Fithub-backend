package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.SaleDto;
import com.example.musinsabackend.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public String createSale(@RequestBody SaleDto saleDto) {
        saleService.createSale(saleDto);
        return "Sale created successfully!";
    }

    @PutMapping("/{id}")
    public String updateSale(@PathVariable Long id, @RequestBody SaleDto saleDto) {
        saleService.updateSale(id, saleDto);
        return "Sale updated successfully!";
    }

    @GetMapping
    public List<SaleDto> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/{id}")
    public SaleDto getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }
}
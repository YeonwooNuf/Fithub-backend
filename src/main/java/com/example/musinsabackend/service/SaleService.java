package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.SaleDto;
import com.example.musinsabackend.model.Sale;
import com.example.musinsabackend.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public void createSale(SaleDto saleDto) {
        Sale sale = new Sale();
        sale.setTitle(saleDto.getTitle());
        sale.setDescription(saleDto.getDescription());
        sale.setPrice(saleDto.getPrice());
        sale.setImageUrl(saleDto.getImageUrl());
        sale.setColor(saleDto.getColor());
        sale.setSize(saleDto.getSize());
        saleRepository.save(sale);
    }

    public void updateSale(Long id, SaleDto saleDto) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found"));
        sale.setTitle(saleDto.getTitle());
        sale.setDescription(saleDto.getDescription());
        sale.setPrice(saleDto.getPrice());
        sale.setImageUrl(saleDto.getImageUrl());
        sale.setColor(saleDto.getColor());
        sale.setSize(saleDto.getSize());
        saleRepository.save(sale);
    }

    public List<SaleDto> getAllSales() {
        return saleRepository.findAll().stream().map(sale -> {
            SaleDto dto = new SaleDto();
            dto.setId(sale.getId());
            dto.setTitle(sale.getTitle());
            dto.setDescription(sale.getDescription());
            dto.setPrice(sale.getPrice());
            dto.setImageUrl(sale.getImageUrl());
            dto.setColor(sale.getColor());
            dto.setSize(sale.getSize());
            return dto;
        }).collect(Collectors.toList());
    }

    public SaleDto getSaleById(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found"));
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setTitle(sale.getTitle());
        dto.setDescription(sale.getDescription());
        dto.setPrice(sale.getPrice());
        dto.setImageUrl(sale.getImageUrl());
        dto.setColor(sale.getColor());
        dto.setSize(sale.getSize());
        return dto;
    }
}
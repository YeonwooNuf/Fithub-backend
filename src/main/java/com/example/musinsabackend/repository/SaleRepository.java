package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
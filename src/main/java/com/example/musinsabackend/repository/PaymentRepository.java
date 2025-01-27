package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

package com.example.musinsabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // 스케쥴러 활성화 (매일 자정 만료 쿠폰 삭제)
public class MusinsaBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusinsaBackendApplication.class, args);
    }
}

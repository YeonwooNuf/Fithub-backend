package com.example.musinsabackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // ✅ 테스트 환경에서 application-test.properties 사용
class MusinsaBackendApplicationTests {
    @Test
    void contextLoads() {
    }
}

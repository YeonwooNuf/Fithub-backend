package com.example.musinsabackend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${SECRET_KEY:default-secret-key}")
    private String SECRET_KEY;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        // ✅ "Bearer " 접두어가 포함되어 있다면 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
            System.out.println("🟡 [JwtTokenProvider] 토큰 검증 성공: " + token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ [JwtTokenProvider] 토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
}

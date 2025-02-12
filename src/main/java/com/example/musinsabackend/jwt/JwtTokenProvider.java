package com.example.musinsabackend.jwt;

import com.example.musinsabackend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${SECRET_KEY:default-secret-key}")
    private String SECRET_KEY;

    // ✅ 보안 강화: 키를 안전하게 생성
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.trim().getBytes(StandardCharsets.UTF_8));
    }

    // ✅ 토큰 생성 (userId 포함)
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("role", "ROLE_USER")  // ✅ 역할 정보 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Claims 추출 (공통 메서드)
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)  // ✅ "Bearer" 제거 코드 삭제
                .getBody();
    }

    // ✅ 토큰에서 username 추출
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ 토큰에서 userId 추출
    public Long getUserIdFromToken(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    // ✅ 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            System.out.println("🟡 [JwtTokenProvider] 토큰 검증 성공: " + token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ [JwtTokenProvider] 토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
}

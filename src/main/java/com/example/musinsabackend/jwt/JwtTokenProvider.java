package com.example.musinsabackend.jwt;

import com.example.musinsabackend.model.User;
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

    // SECRET_KEY를 한 번만 바이트 배열로 변환하여 저장
    private byte[] getSigningKey() {
        return SECRET_KEY.getBytes();
    }

    // 토큰 생성 (userId 포함)
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getUserId()) // ✅ userId 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    // 공통 메서드: 토큰에서 Claims 추출
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token.replace("Bearer ", "")) // ✅ "Bearer " 제거
                .getBody();
    }

    // 토큰에서 username 추출
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ 토큰에서 userId 추출 (새로 추가)
    public Long getUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    // ✅ JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            extractClaims(token); // ✅ 예외 발생 여부로 검증
            System.out.println("🟡 [JwtTokenProvider] 토큰 검증 성공: " + token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ [JwtTokenProvider] 토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
}

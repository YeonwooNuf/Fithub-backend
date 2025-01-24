//package com.example.musinsabackend.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    @Value("${JWT_SECRET}")
//    private String SECRET_KEY;
//
//    // 토큰에서 사용자 이름(username) 추출
//    public String extractUsername(String token) {
//        return extractAllClaims(token).getSubject();
//    }
//
//    // 토큰 유효성 검증
//    public boolean validateToken(String token, String username) {
//        String extractedUsername = extractUsername(token);
//        return extractedUsername.equals(username) && !isTokenExpired(token);
//    }
//
//    // 토큰 만료 여부 확인
//    private boolean isTokenExpired(String token) {
//        return extractAllClaims(token).getExpiration().before(new Date());
//    }
//
//    // Claims 추출
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
//    }
//}

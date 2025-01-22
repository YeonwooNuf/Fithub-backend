package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Value("${JWT_SECRET:default-secret-key}")
    private String SECRET_KEY;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    public void registerUser(UserDto userDto) {
        // 사용자 중복 확인
        Optional<User> existingUser = userRepository.findById(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 해싱
        user.setNickname(userDto.getNickname());
        user.setBirthdate(userDto.getBirthdate());
        user.setPhone(userDto.getPhone());
        user.setGender(userDto.getGender());
        userRepository.save(user);
    }

    // 로그인 처리
    public String loginUser(String username, String plainPassword) {
        User user = userRepository.findById(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(plainPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성 후 반환
        return generateToken(user);
    }

    // JWT 토큰 생성
    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("nickname", user.getNickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    // 사용자 정보 조회
    public User findUserByUsername(String username) {
        return userRepository.findById(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return extractedUsername.equals(username);
        } catch (Exception e) {
            return false;
        }
    }
}

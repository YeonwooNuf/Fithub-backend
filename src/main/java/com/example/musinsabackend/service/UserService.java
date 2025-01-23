package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Value("${SECRET_KEY:default-secret-key}") // 환경 변수에서 키 값 가져옴
    private String SECRET_KEY;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct // 애플리케이션 시작 시 SECRET_KEY 확인
    public void logSecretKey() {
        log.info("Loaded SECRET_KEY: {}", SECRET_KEY);
        if (SECRET_KEY.equals("default-secret-key")) {      // SECRET_KEY 없으면 디폴트 값 지정
            log.warn("deafult 키 값을 사용중입니다. 개인 키로 변경해주세요.");
        }
    }

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
        try {
            User user = userRepository.findById(username).orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 사용자입니다.")
            );

            if (!passwordEncoder.matches(plainPassword, user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            return generateToken(user);
        } catch (IllegalArgumentException e) {
            log.error("로그인 요청 실패: ", e); // 예외 로그 기록
            throw e; // 예외 다시 던짐
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: ", e); // 모든 예외 로그 기록
            throw new RuntimeException("로그인 처리 중 오류 발생"); // 명시적인 예외 반환
        }
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
            log.error("JWT 토큰 검증 실패: ", e);
            return false;
        }
    }

    // token 으로 사용자 조회
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject(); // username 추출
        } catch (Exception e) {
            log.error("토큰에서 사용자 이름 추출 실패: ", e);
            return null; // 실패 시 null 반환
        }
    }

}


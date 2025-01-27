package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public void registerUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 해싱
        user.setNickname(userDto.getNickname());
        user.setProfileImageUrl(
                userDto.getProfileImageUrl() != null ? userDto.getProfileImageUrl() : "default-profile-image-url"
        );

        userRepository.save(user);
    }

    // 로그인
    public String loginUser(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(plainPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generateToken(user.getUsername());
    }

    // 사용자 정보 조회
    public UserDto findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setNickname(user.getNickname());
        userDto.setProfileImageUrl(user.getProfileImageUrl());
        return userDto;
    }
}

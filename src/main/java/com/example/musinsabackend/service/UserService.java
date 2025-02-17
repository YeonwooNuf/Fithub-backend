package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.Role;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.UserCouponRepository;
import com.example.musinsabackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserCouponRepository userCouponRepository;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder passwordEncoder, UserCouponRepository userCouponRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userCouponRepository = userCouponRepository;
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
        user.setGender(userDto.getGender());
        user.setBirthdate(userDto.getBirthdate());
        user.setPhone(userDto.getPhone());
        user.setProfileImageUrl(
                (userDto.getProfileImageUrl() != null && !userDto.getProfileImageUrl().isEmpty())
                        ? userDto.getProfileImageUrl()
                        : "default-profile.jpg"
        );

        user.setRole(userDto.getRole() != null ? userDto.getRole() : Role.USER);

        userRepository.save(user);
    }

    // 로그인
    public String loginUser(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(plainPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generateToken(user);
    }

    // 사용자 정보 조회
    public User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserDto userDto = new UserDto(user);
        userDto.setUserId(user.getUserId()); // ✅ 추가
        userDto.setUsername(user.getUsername());
        userDto.setNickname(user.getNickname());
        userDto.setProfileImageUrl(user.getProfileImageUrl());
        userDto.setGender(user.getGender());
        userDto.setBirthdate(user.getBirthdate());
        userDto.setPhone(user.getPhone());

        userDto.setCoupons(user.getUserCoupons().stream()  // ✅ UserCoupon으로 변경
                .map(userCoupon -> new CouponDto(
                        userCoupon.getCoupon().getId(),
                        userCoupon.getCoupon().getName(),
                        userCoupon.getCoupon().getDiscount(),
                        userCoupon.getExpiryDate(),
                        userCoupon.isUsed()  // ✅ 오류 해결
                ))
                .toList());


        userDto.setPoints(user.getPoints().stream()
                .map(point -> new PointDto(point.getId(), point.getDescription(), point.getAmount(), point.getDate()))
                .toList());

        return user;
    }

    // 사용자 ID로 사용자 정보 조회
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 쿠폰 개수 직접 가져오기
    public int getUserCouponCount(Long userId) {
        return userCouponRepository.countCouponsByUserId(userId);
    }

    // 전체 사용자 조회 (페이지네이션) - username, nickname, birthdate, phone, gender 포함
    public Page<UserDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(UserDto::new);
    }

    // ✅ 특정 사용자 검색 (닉네임 또는 이메일 기반)
    public UserDto searchUser(String query) {
        Optional<User> userOpt = userRepository.findByUsername(query);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByNickname(query);
        }

        User user = userOpt.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 수정된 부분: UserDto 생성 시, user 객체를 넘겨줌
        return new UserDto(user);
    }

    @Transactional
    // ✅ 사용자 삭제 (Hard Delete)
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        userRepository.deleteById(userId);
    }
}

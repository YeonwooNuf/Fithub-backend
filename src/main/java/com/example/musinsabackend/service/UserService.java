package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.user.AuthProvider;
import com.example.musinsabackend.model.user.Role;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.PointRepository;
import com.example.musinsabackend.repository.user.CouponRepository;
import com.example.musinsabackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CouponRepository couponRepository;
    private final PointRepository pointRepository;
    private final FileUploadService fileUploadService;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder passwordEncoder, CouponRepository couponRepository, PointRepository pointRepository, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.couponRepository = couponRepository;
        this.pointRepository = pointRepository;
        this.fileUploadService = fileUploadService;
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
        user.setProvider(AuthProvider.LOCAL);
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
    public UserDto findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserDto userDto = new UserDto(user);
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setNickname(user.getNickname());
        userDto.setProfileImageUrl(user.getProfileImageUrl());
        userDto.setGender(user.getGender());
        userDto.setBirthdate(user.getBirthdate());
        userDto.setPhone(user.getPhone());

        userDto.setCoupons(user.getUserCoupons().stream()
                .map(userCoupon -> new CouponDto(
                        userCoupon.getCoupon().getId(),
                        userCoupon.getCoupon().getName(),
                        userCoupon.getCoupon().getDiscount(),
                        userCoupon.getExpiryDate(),
                        userCoupon.isUsed()
                ))
                .toList());

        // ✅ 포인트 정보 추가
        userDto.setPoints(pointRepository.findByUser_UserId(user.getUserId(), Pageable.unpaged()).stream()
                .map(point -> new PointDto(
                        point.getId(),
                        user.getUserId(),
                        point.getAmount(),
                        point.getStatus(),
                        point.getReason() != null ? point.getReason().name() : "기타",
                        // ENUM -> String 변환
                        point.getCreatedAt(),
                        point.getExpiredAt(),
                        point.getOrder() != null ? point.getOrder().getId() : null
                ))
                .toList());
        return userDto;
    }


    // 사용자 ID로 사용자 정보 조회
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserDto userDto = new UserDto(user); // 주소만 세팅됨

        // ✅ 쿠폰 목록 null 체크 후 설정
        if (user.getUserCoupons() != null) {
            userDto.setCoupons(
                    user.getUserCoupons().stream()
                            .filter(uc -> uc.getCoupon() != null) // ✅ null 쿠폰 방어
                            .map(uc -> new CouponDto(
                                    uc.getCoupon().getId(),
                                    uc.getCoupon().getName(),
                                    uc.getCoupon().getDiscount(),
                                    uc.getExpiryDate(),
                                    uc.isUsed()
                            ))
                            .toList()
            );
        } else {
            userDto.setCoupons(List.of()); // 빈 리스트로 초기화
        }

        // ✅ 포인트 목록 설정 (결과가 null일 일은 없지만 방어코드로 비워도 괜찮음)
        try {
            userDto.setPoints(
                    pointRepository.findByUser_UserId(user.getUserId(), Pageable.unpaged())
                            .stream()
                            .map(point -> new PointDto(
                                    point.getId(),
                                    user.getUserId(),
                                    point.getAmount(),
                                    point.getStatus(),
                                    point.getReason() != null ? point.getReason().name() : "기타",
                                    point.getCreatedAt(),
                                    point.getExpiredAt(),
                                    point.getOrder() != null ? point.getOrder().getId() : null
                            ))
                            .toList()
            );
        } catch (Exception e) {
            System.out.println("❌ 포인트 조회 중 오류 발생: " + e.getMessage());
            userDto.setPoints(List.of()); // 빈 리스트 설정
        }

        return userDto;
    }

    // 쿠폰 개수 직접 가져오기
    public int getUserCouponCount(Long userId) {
        return couponRepository.countCouponsByUserId(userId);
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

    // ✅ 변경
    public UserDto findUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserDto(user);
    }

    @Transactional
    public void updateUserInfo(Long userId, String nickname, String phone, String gender, String birthdate, String newPassword, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (nickname != null) user.setNickname(nickname);
        if (phone != null) user.setPhone(phone);
        if (gender != null) user.setGender(gender);
        if (birthdate != null) user.setBirthdate(birthdate);
        if (newPassword != null && !newPassword.isEmpty()) user.setPassword(passwordEncoder.encode(newPassword));

        if (profileImage != null && !profileImage.isEmpty()) {
            String filename = fileUploadService.saveProfileImage(profileImage);
            user.setProfileImageUrl(filename);
        }

        userRepository.save(user);
    }

}

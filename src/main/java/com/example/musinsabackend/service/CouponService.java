package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.coupon.CouponDistributionType;
import com.example.musinsabackend.model.coupon.UserCoupon;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.admin.AdminCouponRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.user.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponService {

    @Autowired
    private AdminCouponRepository adminCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    // ✅ 사용자 보유 쿠폰 목록 조회 (만료된 쿠폰 필터링 후 반환)
    public List<CouponDto> getUserCoupons(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        List<UserCoupon> userCoupons = couponRepository.findUserCouponsByUser(user);

//        // 만료된 쿠폰 삭제
//        userCoupons.removeIf(userCoupon -> {
//            if (userCoupon.getExpiryDate().isBefore(LocalDate.now()) || userCoupon.isUsed()) {
//                userCouponRepository.delete(userCoupon);
//                return true;
//            }
//            return false;
//        });

        return userCoupons.stream()
                .filter(userCoupon -> userCoupon.getExpiryDate().isAfter(LocalDate.now()))  // 만료된 쿠폰 필터링
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ✅ 마이페이지에서 현재 보유 쿠폰 개수 조회
    public int getCouponCount(Long userId) {
        return couponRepository.countCouponsByUserId(userId);
    }

    // ✅ 쿠폰 등록 (자동 지급, 수동 등록)
    public void createCoupon(CouponDto couponDto) {
        Coupon coupon = new Coupon();
        coupon.setName(couponDto.getName());
        coupon.setDiscount(couponDto.getDiscount());
        coupon.setMaxDiscountAmount(couponDto.getMaxDiscountAmount());
        coupon.setDescription(couponDto.getDescription());
        coupon.setExpiryDate(couponDto.getExpiryDate());
        coupon.setTarget(couponDto.getTarget());
        coupon.setTargetValue(couponDto.getTargetValue());
        coupon.setDistributionType(couponDto.getDistributionType());
        coupon.setCouponCode(couponDto.getCouponCode());

        // ✅ 자동 지급 쿠폰은 couponCode를 null로 처리
        if (couponDto.getDistributionType() == CouponDistributionType.AUTO) {
            coupon.setCouponCode(null);
        } else {
            coupon.setCouponCode(couponDto.getCouponCode().toUpperCase());
        }

        adminCouponRepository.save(coupon);

        // 자동 지급 쿠폰인 경우 모든 사용자에게 발급
        if (couponDto.getDistributionType() == CouponDistributionType.AUTO) {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setUser(user);
                userCoupon.setCoupon(coupon);
                userCoupon.setIssuedDate(LocalDate.now());
                userCoupon.setExpiryDate(coupon.getExpiryDate());
                userCoupon.setUsed(false);

                couponRepository.save(userCoupon);
            }
        }
        log.info("✅ 쿠폰 등록 완료: 쿠폰 이름 = {}", coupon.getName());
    }

    // ✅ 쿠폰 수정 (사용자 보유 쿠폰에도 반영)
    public void updateCoupon(Long couponId, CouponDto updatedCouponDto) {
        Coupon coupon = adminCouponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다."));

        coupon.setName(updatedCouponDto.getName());
        coupon.setDiscount(updatedCouponDto.getDiscount());
        coupon.setMaxDiscountAmount(updatedCouponDto.getMaxDiscountAmount());
        coupon.setDescription(updatedCouponDto.getDescription());
        coupon.setExpiryDate(updatedCouponDto.getExpiryDate());
        coupon.setTarget(updatedCouponDto.getTarget());
        coupon.setTargetValue(updatedCouponDto.getTargetValue());
        adminCouponRepository.save(coupon);

        // ✅ 사용자 보유 쿠폰 정보 동기화 (CouponRepository 사용)
        List<UserCoupon> userCoupons = adminCouponRepository.findUserCouponsByCoupon(coupon);
        for (UserCoupon userCoupon : userCoupons) {
            userCoupon.setExpiryDate(updatedCouponDto.getExpiryDate());
            couponRepository.save(userCoupon);
        }
    }

    // ✅ 쿠폰 삭제
    public void deleteCoupon(Long couponId) {
        Coupon coupon = adminCouponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다."));

        // ✅ 사용자 보유 쿠폰 삭제 (CouponRepository 사용)
        List<UserCoupon> userCoupons = adminCouponRepository.findUserCouponsByCoupon(coupon);
        for (UserCoupon userCoupon : userCoupons) {
            couponRepository.delete(userCoupon);
        }

        adminCouponRepository.delete(coupon);
    }

    // ✅ 현재 등록된 쿠폰 목록 조회
    public List<CouponDto> getAllCoupons() {
        return adminCouponRepository.findAll().stream()
                .map(coupon -> new CouponDto(
                        coupon.getId(),
                        coupon.getName(),
                        coupon.getDiscount(),
                        coupon.getMaxDiscountAmount(),
                        coupon.getDescription(),
                        LocalDate.now(), // issuedDate (현재 날짜로 가정)
                        coupon.getExpiryDate(),
                        false,
                        coupon.getTarget(),
                        coupon.getTargetValue(),
                        coupon.getDistributionType(),
                        coupon.getCouponCode()
                ))
                .collect(Collectors.toList());
    }

    // ✅ 사용자 쿠폰 코드 등록 (이벤트 페이지에서 받은 쿠폰 등록)
    public CouponDto registerCouponByCode(Long userId, String couponCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Coupon coupon = adminCouponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰 코드입니다."));

        if (coupon.getDistributionType() != CouponDistributionType.MANUAL) {
            throw new IllegalArgumentException("이 쿠폰은 수동 등록이 불가능한 쿠폰입니다.");
        }

        boolean alreadyRegistered = couponRepository.existsByUserAndCoupon(user, coupon);
        if (alreadyRegistered) {
            throw new IllegalArgumentException("이미 등록된 쿠폰입니다.");
        }

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("이 쿠폰은 만료되었습니다.");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setIssuedDate(LocalDate.now());
        userCoupon.setExpiryDate(coupon.getExpiryDate());
        userCoupon.setUsed(false);

        couponRepository.save(userCoupon);

        // ✅ 등록된 쿠폰 정보를 CouponDto로 변환하여 반환
        return convertToDto(userCoupon);
    }

    // ✅ 관리자용: 만료된 쿠폰 조회
    public List<CouponDto> getExpiredCoupons() {
        LocalDate today = LocalDate.now();
        List<Coupon> expiredCoupons = adminCouponRepository.findByExpiryDateBefore(today);
        return expiredCoupons.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // ✅ 관리자용: 유효한 쿠폰 조회
    public List<CouponDto> getValidCoupons() {
        LocalDate today = LocalDate.now();
        List<Coupon> validCoupons = adminCouponRepository.findByExpiryDateAfterOrExpiryDateEquals(today, today);
        return validCoupons.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // ✅ 임시로 오류 방지용 메서드 추가 (결제 기능 구현 전)
    public Coupon validateAndUseCoupon(Long userId, Long couponId) {
        return new Coupon();  // 빈 Coupon 객체 반환 (임시 처리)
    }

    // ✅ User 쿠폰용 DTO 변환
    private CouponDto convertToDto(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();
        return new CouponDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscount(),
                coupon.getMaxDiscountAmount(),
                coupon.getDescription(),
                userCoupon.getIssuedDate(),
                userCoupon.getExpiryDate(),
                userCoupon.isUsed(),
                coupon.getTarget(),
                coupon.getTargetValue(),
                coupon.getDistributionType(),
                coupon.getCouponCode()
        );
    }

    // ✅ 새로운 Coupon용 DTO 변환
    private CouponDto convertToDto(Coupon coupon) {
        return new CouponDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscount(),
                coupon.getMaxDiscountAmount(),
                coupon.getDescription(),
                null, // UserCoupon이 없으므로 issuedDate는 null 처리
                coupon.getExpiryDate(),
                false, // 기본적으로 사용되지 않음
                coupon.getTarget(),
                coupon.getTargetValue(),
                coupon.getDistributionType(),
                coupon.getCouponCode()
        );
    }
}
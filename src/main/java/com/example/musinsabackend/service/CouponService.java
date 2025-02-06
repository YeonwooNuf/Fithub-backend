package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.model.*;
import com.example.musinsabackend.repository.CouponRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.UserCouponRepository;
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
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    // ✅ 사용자 보유 쿠폰 목록 조회 (만료된 쿠폰 자동 삭제)
    public List<CouponDto> getUserCoupons(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        List<UserCoupon> userCoupons = userCouponRepository.findUserCouponsByUser(user);

        // 만료된 쿠폰 삭제
        userCoupons.removeIf(userCoupon -> {
            if (userCoupon.getExpiryDate().isBefore(LocalDate.now()) || userCoupon.isUsed()) {
                userCouponRepository.delete(userCoupon);
                return true;
            }
            return false;
        });

        return userCoupons.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // ✅ 마이페이지에서 현재 보유 쿠폰 개수 조회
    public int getCouponCount(Long userId) {
        return userCouponRepository.countCouponsByUserId(userId);
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

        couponRepository.save(coupon);

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

                userCouponRepository.save(userCoupon);
            }
        }

        log.info("✅ 쿠폰 등록 완료: 쿠폰 이름 = {}", coupon.getName());
    }

    // ✅ 쿠폰 수정 (사용자 보유 쿠폰에도 반영)
    public void updateCoupon(Long couponId, CouponDto updatedCouponDto) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다."));

        coupon.setName(updatedCouponDto.getName());
        coupon.setDiscount(updatedCouponDto.getDiscount());
        coupon.setMaxDiscountAmount(updatedCouponDto.getMaxDiscountAmount());
        coupon.setDescription(updatedCouponDto.getDescription());
        coupon.setExpiryDate(updatedCouponDto.getExpiryDate());
        coupon.setTarget(updatedCouponDto.getTarget());
        coupon.setTargetValue(updatedCouponDto.getTargetValue());
        couponRepository.save(coupon);

        // ✅ 사용자 보유 쿠폰 정보 동기화 (CouponRepository 사용)
        List<UserCoupon> userCoupons = couponRepository.findUserCouponsByCoupon(coupon);
        for (UserCoupon userCoupon : userCoupons) {
            userCoupon.setExpiryDate(updatedCouponDto.getExpiryDate());
            userCouponRepository.save(userCoupon);
        }

        log.info("✅ 쿠폰 정보 수정 완료: 쿠폰 ID = {}", couponId);
    }

    // ✅ 쿠폰 삭제
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다."));

        // ✅ 사용자 보유 쿠폰 삭제 (CouponRepository 사용)
        List<UserCoupon> userCoupons = couponRepository.findUserCouponsByCoupon(coupon);
        for (UserCoupon userCoupon : userCoupons) {
            userCouponRepository.delete(userCoupon);
        }

        couponRepository.delete(coupon);
        log.info("✅ 쿠폰 삭제 완료: 쿠폰 ID = {}", couponId);
    }


    // ✅ 현재 등록된 쿠폰 목록 조회
    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream()
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

    // ✅ 임시로 오류 방지용 메서드 추가 (결제 기능 구현 전)
    public Coupon validateAndUseCoupon(Long userId, Long couponId) {
        return new Coupon();  // 빈 Coupon 객체 반환 (임시 처리)
    }

    // ✅ 쿠폰 DTO 변환
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
}
package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.model.Coupon;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.CouponRepository;
import com.example.musinsabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 사용자 보유 쿠폰 조회 (userId 기반)
    public List<CouponDto> getUserCoupons(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        List<Coupon> coupons = couponRepository.findByUser(user);

        return coupons.stream().map(coupon -> {
            CouponDto dto = new CouponDto();
            dto.setId(coupon.getId());
            dto.setName(coupon.getName());
            dto.setDiscount(coupon.getDiscount());

            // ✅ LocalDate -> String 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dto.setExpiryDate(LocalDate.parse(coupon.getExpiryDate().format(formatter)));

            dto.setUsed(coupon.isUsed());
            return dto;
        }).collect(Collectors.toList());
    }

    // ✅ 쿠폰 유효성 검사
    private void validateCoupon(Coupon coupon) {
        LocalDate today = LocalDate.now();
        if (coupon.getExpiryDate().isBefore(today)) {
            throw new IllegalArgumentException("쿠폰이 만료되었습니다.");
        }

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }
    }

    // ✅ 쿠폰 사용 처리 (userId 기반)
    public void useCoupon(Long userId, Long couponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Coupon coupon = couponRepository.findByIdAndUser(couponId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다."));

        // ✅ 쿠폰 유효성 검사
        validateCoupon(coupon);

        // ✅ 쿠폰 사용 처리
        coupon.setUsed(true);
        couponRepository.save(coupon);

        log.info("✅ 쿠폰 사용 완료: 쿠폰 ID = {}, 사용자 ID = {}", couponId, userId);
    }

    public Coupon validateAndUseCoupon(Long userId, Long couponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        Coupon coupon = couponRepository.findByIdAndUser(couponId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰을 찾을 수 없습니다."));

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }

        coupon.setUsed(true);
        couponRepository.save(coupon);
        return coupon;
    }

}

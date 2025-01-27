package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.Coupon;
import com.example.musinsabackend.repository.CouponRepository;
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
    private JwtTokenProvider jwtTokenProvider;

    // 사용자 보유 쿠폰 조회
    public List<CouponDto> getUserCoupons(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);

        List<Coupon> coupons = couponRepository.findByUser_Username(username);

        return coupons.stream().map(coupon -> {
            CouponDto dto = new CouponDto();
            dto.setId(coupon.getId());
            dto.setName(coupon.getName());
            dto.setDiscount(coupon.getDiscount());

            // LocalDate -> String 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dto.setExpiryDate(coupon.getExpiryDate().format(formatter));

            dto.setUsed(coupon.isUsed());
            return dto;
        }).collect(Collectors.toList());
    }

    // 쿠폰 유효성 검사
    private void validateCoupon(Coupon coupon) {
        LocalDate today = LocalDate.now();
        if (coupon.getExpiryDate().isBefore(today)) {
            throw new IllegalArgumentException("쿠폰이 만료되었습니다.");
        }

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }
    }

    // 쿠폰 사용 처리
    public void useCoupon(String token, Long couponId) {
        String username = jwtTokenProvider.getUsernameFromToken(token);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다."));

        // 쿠폰 소유 여부 확인
        if (!coupon.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("해당 쿠폰은 사용자가 소유하지 않았습니다.");
        }

        // 쿠폰 유효성 검사
        validateCoupon(coupon);

        // 쿠폰 사용 처리
        coupon.setUsed(true);
        couponRepository.save(coupon);

        log.info("쿠폰 사용 완료: 쿠폰 ID = {}, 사용자 = {}", couponId, username);
    }

    public Coupon validateAndUseCoupon(String username, Long couponId) {
        // 쿠폰 유효성 확인
        Coupon coupon = couponRepository.findByIdAndUser_Username(couponId, username)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰입니다."));

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }

        // 쿠폰 사용 처리
        coupon.setUsed(true);
        couponRepository.save(coupon);

        return coupon;
    }
}

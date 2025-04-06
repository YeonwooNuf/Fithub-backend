package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.coupon.UserCoupon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCouponDto {
    private Long id;
    private CouponDto coupon;

    public UserCouponDto(UserCoupon userCoupon) {
        this.id = userCoupon.getId();

        // CouponDto 수동 생성 (UserCoupon과 연계된 정보 포함)
        CouponDto dto = new CouponDto();
        dto.setId(userCoupon.getCoupon().getId());
        dto.setName(userCoupon.getCoupon().getName());
        dto.setDiscount(userCoupon.getCoupon().getDiscount());
        dto.setMaxDiscountAmount(userCoupon.getCoupon().getMaxDiscountAmount());
        dto.setDescription(userCoupon.getCoupon().getDescription());
        dto.setExpiryDate(userCoupon.getCoupon().getExpiryDate());
        dto.setIssuedDate(userCoupon.getIssuedDate());  // ⭐ 발급일은 여기서 설정!
        dto.setUsed(userCoupon.isUsed());               // ⭐ 사용 여부도 설정!
        dto.setTarget(userCoupon.getCoupon().getTarget());
        dto.setTargetValue(userCoupon.getCoupon().getTargetValue());
        dto.setDistributionType(userCoupon.getCoupon().getDistributionType());
        dto.setCouponCode(userCoupon.getCoupon().getCouponCode());

        this.coupon = dto;
    }
}

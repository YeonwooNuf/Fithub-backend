package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // ✅ 기본 생성자 추가
public class AddressDto {
    private Long id;
    private String zonecode; // ✅ 우편번호
    private String roadAddress; // ✅ 도로명 주소
    private String jibunAddress; // ✅ 지번 주소
    private String detailAddress; // ✅ 상세 주소
    private String reference; // ✅ 참고항목
    private boolean isDefault; // ✅ 기본 배송지 여부

    // ✅ 엔티티를 DTO로 변환하는 생성자
    public AddressDto(Address address) {
        this.id = address.getId();
        this.zonecode = address.getZonecode();
        this.roadAddress = address.getRoadAddress();
        this.jibunAddress = address.getJibunAddress();
        this.detailAddress = address.getDetailAddress();
        this.reference = address.getReference();
        this.isDefault = address.isDefault();
    }
}

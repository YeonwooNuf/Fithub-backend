package com.example.musinsabackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "address") // ✅ 테이블명 "address" 사용
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // ✅ 사용자와 연관 관계 설정
    private User user; // 사용자 정보

    @Column(nullable = false)
    private String postCode; // ✅ 우편번호

    @Column(nullable = false)
    private String roadAddress; // ✅ 도로명 주소

    @Column(nullable = false)
    private String jibunAddress; // ✅ 지번 주소

    @Column(nullable = true)
    private String detailAddress; // ✅ 상세 주소

    @Column(nullable = true)
    private String reference; // ✅ 참고항목

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDefault; // ✅ 기본 배송지 여부

    public Address(User user, String postCode, String roadAddress, String jibunAddress, String detailAddress, String reference) {
        this.user = user;
        this.postCode = postCode;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
        this.reference = reference;
    }

    public Address() {
    }

    // ✅ 주소 업데이트 메서드
    public void updateAddress(String postCode, String roadAddress, String jibunAddress, String detailAddress, String reference) {
        this.postCode = postCode;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
        this.reference = reference;
    }
}

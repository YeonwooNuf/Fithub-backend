package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.user.Role;
import com.example.musinsabackend.model.user.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long userId;
    private String password;
    private String username;
    private String nickname;
    private String birthdate;
    private String phone;
    private String gender;
    private String profileImageUrl;
    private Role role;
    private List<AddressDto> addresses; // ✅ 사용자 주소 목록 추가

    private List<OrderDto> orders;
    private List<ReviewDto> reviews;
    private List<AskingDto> askings;
    private List<CommunityPostDto> communityPosts;
    private List<CouponDto> coupons;
    private List<PointDto> points;

    // 기본 생성자 추가
    public UserDto() {
    }

    // ✅ User 엔티티 → UserDto 변환하는 생성자 추가
    public UserDto(User user) {
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.birthdate = user.getBirthdate();
        this.phone = user.getPhone();
        this.gender = user.getGender();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole();

        // ✅ User 엔티티의 List<Address> → List<AddressDto> 변환
        this.addresses = user.getAddresses().stream()
                .map(AddressDto::new) // Address 엔티티를 AddressDto로 변환
                .collect(Collectors.toList());
    }
}

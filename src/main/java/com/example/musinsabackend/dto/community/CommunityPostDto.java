package com.example.musinsabackend.dto.community;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.community.CommunityPost;
import com.example.musinsabackend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostDto {

    private Long id;

    private String content;

    private LocalDateTime createdAt;

    private Long userId;
    private String nickname;
    private String profileImageUrl;

    private List<String> imageUrls; // 게시글 이미지

    private List<ProductDto> products; // 연결된 여러 상품

    private int likeCount;

    public static CommunityPostDto from(CommunityPost post, List<String> imageUrls, List<ProductDto> productDtos) {
        User user = post.getUser();
        String profileImage = user.getProfileImageUrl();
        String profileImageUrl;

        if (profileImage != null && profileImage.trim().startsWith("http")) {
            profileImageUrl = profileImage.trim(); // 외부 URL
        } else {
            profileImageUrl = "/uploads/profile-images/" +
                    (profileImage != null ? profileImage.trim() : "default-profile.jpg");
        }

        return CommunityPostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(profileImageUrl)  // ✅ 수정된 부분
                .imageUrls(imageUrls)
                .products(productDtos)
                .likeCount(post.getLikes().size())
                .build();
    }
}

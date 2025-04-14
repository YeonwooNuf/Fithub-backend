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
        return CommunityPostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl("/uploads/profile-images/" + user.getProfileImageUrl())
                .imageUrls(imageUrls)
                .products(productDtos)
                .likeCount(post.getLikes().size()) // ✅ 추가
                .build();
    }
}

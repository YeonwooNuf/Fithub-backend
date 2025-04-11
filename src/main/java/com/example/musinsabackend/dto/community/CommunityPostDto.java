package com.example.musinsabackend.dto.community;

import com.example.musinsabackend.model.community.CommunityPost;
import com.example.musinsabackend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    private Long productId;
    private String productName;

    private List<String> imageUrls;

    private String profileImageUrl;

    public static CommunityPostDto from(CommunityPost post, List<String> imageUrls) {
        return CommunityPostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userId(post.getUser().getUserId())
                .nickname(post.getUser().getNickname()) // 👈 사용자 닉네임
                .profileImageUrl("/uploads/profile-images/" + post.getUser().getProfileImageUrl())
                .productId(post.getLinkedProduct() != null ? post.getLinkedProduct().getId() : null)
                .productName(post.getLinkedProduct() != null ? post.getLinkedProduct().getName() : null)
                .imageUrls(imageUrls)
                .build();
    }
}

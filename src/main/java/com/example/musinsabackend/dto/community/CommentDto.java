package com.example.musinsabackend.dto.community;

import com.example.musinsabackend.model.community.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    private Long userId;
    private String nickname;
    private String profileImageUrl;

    private Long parentId; // 대댓글일 경우만 값 존재

    // ✅ static 변환 메서드
    public static CommentDto from(Comment comment) {
        String profileImage = comment.getUser().getProfileImageUrl();
        String profileImageUrl;

        if (profileImage != null && profileImage.startsWith("http")) {
            // 외부 이미지 (ex. 카카오)
            profileImageUrl = profileImage;
        } else {
            // 내부 업로드 이미지 또는 null
            profileImageUrl = "/uploads/profile-images/" +
                    (profileImage != null ? profileImage : "default-profile.jpg");
        }

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(profileImageUrl)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .build();
    }
}

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

    private Long parentId; // 대댓글일 경우만 값 존재

    // ✅ static 변환 메서드
    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getUserId())
                .nickname(comment.getUser().getNickname()) // 필요시 getNickname() 등으로 수정
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .build();
    }
}

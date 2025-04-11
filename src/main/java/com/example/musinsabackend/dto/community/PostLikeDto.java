package com.example.musinsabackend.dto.community;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeDto {
    private Long postId;
    private Long userId;
    private boolean liked; // true: 좋아요 상태, false: 취소됨
}

package com.example.musinsabackend.service.community;

import com.example.musinsabackend.dto.community.PostLikeDto;
import com.example.musinsabackend.model.community.CommunityPost;
import com.example.musinsabackend.model.community.PostLike;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.community.CommunityPostRepository;
import com.example.musinsabackend.repository.community.PostLikeRepository;
import com.example.musinsabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final CommunityPostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final UserRepository userRepository;

    public PostLikeDto toggleLike(Long postId, Long userId) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Optional<PostLike> existing = likeRepository.findByCommunityPost_IdAndUser_UserId(postId, userId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return new PostLikeDto(postId, userId, false);
        } else {
            PostLike like = new PostLike();
            like.setCommunityPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return new PostLikeDto(postId, userId, true);
        }
    }

    public int countLikes(Long postId) {
        return likeRepository.countByCommunityPost_Id(postId);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        return likeRepository.existsByCommunityPost_IdAndUser_UserId(postId, userId);
    }
}

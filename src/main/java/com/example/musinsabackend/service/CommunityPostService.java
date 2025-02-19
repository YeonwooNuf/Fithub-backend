package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.CommunityPostDto;
import com.example.musinsabackend.model.CommunityPost;
import com.example.musinsabackend.model.PostLike;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.CommunityPostRepository;
import com.example.musinsabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommunityPostService {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private UserRepository userRepository;

    // 게시글 생성
    public CommunityPostDto createPost(String username, CommunityPostDto postDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setContent(postDto.getContent());
        post.setImageUrl(postDto.getImageUrl());
        post.setCreatedAt(LocalDateTime.now());

        CommunityPost savedPost = communityPostRepository.save(post);

        return mapToDto(savedPost);
    }

    // 전체 게시글 조회
    public List<CommunityPostDto> getAllPosts() {
        List<CommunityPost> posts = communityPostRepository.findAll();
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // 특정 게시글 조회
    public CommunityPostDto getPostById(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        return mapToDto(post);
    }

    // 게시글 수정
    public CommunityPostDto updatePost(String username, Long postId, CommunityPostDto updatedPostDto) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("작성자가 아닌 사용자는 게시글을 수정할 수 없습니다.");
        }

        post.setContent(updatedPostDto.getContent());
        post.setImageUrl(updatedPostDto.getImageUrl());
        CommunityPost updatedPost = communityPostRepository.save(post);

        return mapToDto(updatedPost);
    }

    // 게시글 삭제
    public void deletePost(String username, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("작성자가 아닌 사용자는 게시글을 삭제할 수 없습니다.");
        }

        communityPostRepository.delete(post);
    }

    // 좋아요 추가
    public void likePost(String username, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getUsername().equals(username));

        if (alreadyLiked) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        post.getLikes().add(like);
        communityPostRepository.save(post);
    }

    // 좋아요 취소
    public void unlikePost(String username, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        PostLike likeToRemove = post.getLikes().stream()
                .filter(like -> like.getUser().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 게시글입니다."));

        post.getLikes().remove(likeToRemove);
        communityPostRepository.save(post);
    }

    // CommunityPost -> CommunityPostDto 매핑
    private CommunityPostDto mapToDto(CommunityPost post) {
        CommunityPostDto dto = new CommunityPostDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setUsername(post.getUser().getUsername());
        dto.setLikeCount(post.getLikes().size());

        // LocalDateTime -> String 변환
        if (post.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dto.setCreatedAt(post.getCreatedAt().format(formatter));
        } else {
            dto.setCreatedAt(null);
        }

        return dto;
    }
}

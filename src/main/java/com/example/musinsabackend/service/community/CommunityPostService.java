package com.example.musinsabackend.service.community;

import com.example.musinsabackend.dto.community.CommunityPostDto;
import com.example.musinsabackend.model.community.CommunityPost;
import com.example.musinsabackend.model.community.PostImage;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.user.ProductRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.community.PostImageRepository;
import com.example.musinsabackend.repository.community.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final PostImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final String uploadDir = "/app/uploads/snaps/";

    public CommunityPostDto createPost(Long userId, String content, Long productId, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = (productId != null) ? productRepository.findById(productId).orElse(null) : null;

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setContent(content);
        post.setLinkedProduct(product);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        List<String> imageUrls = saveImages(post, images);
        return CommunityPostDto.from(post, imageUrls);
    }

    public List<CommunityPostDto> getAllPosts() {
        List<CommunityPost> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<CommunityPostDto> result = new ArrayList<>();
        for (CommunityPost post : posts) {
            List<String> imageUrls = imageRepository.findByCommunityPost_Id(post.getId())
                    .stream().map(PostImage::getImageUrl).toList();
            result.add(CommunityPostDto.from(post, imageUrls));
        }
        return result;
    }

    public CommunityPostDto getPostDetail(Long postId) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        List<String> imageUrls = imageRepository.findByCommunityPost_Id(postId)
                .stream().map(PostImage::getImageUrl).toList();
        return CommunityPostDto.from(post, imageUrls);
    }

    public CommunityPostDto updatePost(Long postId, Long userId, String content, Long productId, List<MultipartFile> images) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        if (!Objects.equals(post.getUser().getUserId(), userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        post.setContent(content);
        post.setLinkedProduct((productId != null) ? productRepository.findById(productId).orElse(null) : null);
        postRepository.save(post);

        // 기존 이미지 삭제
        imageRepository.deleteAll(post.getImages());
        post.getImages().clear();

        // 새 이미지 저장
        List<String> imageUrls = saveImages(post, images);
        return CommunityPostDto.from(post, imageUrls);
    }

    public void deletePost(Long postId, Long userId) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        if (!Objects.equals(post.getUser().getUserId(), userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        // 실제 이미지 파일 삭제 (옵션)
        for (PostImage img : post.getImages()) {
            try {
                Files.deleteIfExists(Paths.get(img.getImageUrl().replace("/uploads/snaps/", uploadDir)));
            } catch (IOException ignored) {}
        }

        postRepository.delete(post);
    }

    private List<String> saveImages(CommunityPost post, List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filepath = Paths.get(uploadDir, filename);
                try {
                    Files.createDirectories(filepath.getParent());
                    file.transferTo(filepath.toFile());
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패", e);
                }

                String url = "/uploads/snaps/" + filename;

                PostImage img = new PostImage();
                img.setCommunityPost(post);
                img.setImageUrl(url);
                imageRepository.save(img);

                imageUrls.add(url);
            }
        }
        return imageUrls;
    }

}

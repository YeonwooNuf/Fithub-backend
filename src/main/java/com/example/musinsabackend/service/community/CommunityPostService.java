package com.example.musinsabackend.service.community;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.dto.community.CommunityPostDto;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.community.CommunityPost;
import com.example.musinsabackend.model.community.PostImage;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.community.CommunityPostRepository;
import com.example.musinsabackend.repository.community.PostImageRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final PostImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/snaps/";

    public CommunityPostDto createPost(Long userId, String content, List<Long> productIds, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Product> products = productIds != null ? productRepository.findAllById(productIds) : List.of();

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setLinkedProducts(products);
        postRepository.save(post);

        List<String> imageUrls = saveImages(post, images);
        List<ProductDto> productDtos = convertToProductDtos(products);
        return CommunityPostDto.from(post, imageUrls, productDtos);
    }

    public List<CommunityPostDto> getAllPosts() {
        List<CommunityPost> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<CommunityPostDto> result = new ArrayList<>();

        for (CommunityPost post : posts) {
            List<String> imageUrls = imageRepository.findByCommunityPost_Id(post.getId())
                    .stream().map(PostImage::getImageUrl).toList();
            List<ProductDto> productDtos = convertToProductDtos(post.getLinkedProducts());
            result.add(CommunityPostDto.from(post, imageUrls, productDtos));
        }

        return result;
    }

    public CommunityPostDto getPostDetail(Long postId) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        List<String> imageUrls = imageRepository.findByCommunityPost_Id(postId)
                .stream().map(PostImage::getImageUrl).toList();
        List<ProductDto> productDtos = convertToProductDtos(post.getLinkedProducts());
        return CommunityPostDto.from(post, imageUrls, productDtos);
    }

    public CommunityPostDto updatePost(Long postId, Long userId, String content, List<Long> productIds, List<MultipartFile> images) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        if (!Objects.equals(post.getUser().getUserId(), userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        List<Product> products = productIds != null ? productRepository.findAllById(productIds) : List.of();
        post.setContent(content);
        post.setLinkedProducts(products);
        postRepository.save(post);

        imageRepository.deleteAll(post.getImages());
        post.getImages().clear();

        List<String> imageUrls = saveImages(post, images);
        List<ProductDto> productDtos = convertToProductDtos(products);
        return CommunityPostDto.from(post, imageUrls, productDtos);
    }

    public void deletePost(Long postId, Long userId) {
        CommunityPost post = postRepository.findById(postId).orElseThrow();
        if (!Objects.equals(post.getUser().getUserId(), userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        for (PostImage img : post.getImages()) {
            try {
                Files.deleteIfExists(Paths.get(uploadDir, img.getImageUrl().substring("/uploads/snaps/".length())));
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

    private List<ProductDto> convertToProductDtos(List<Product> products) {
        return products.stream()
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getImages(),
                        product.getSizes(),
                        product.getColors(),
                        product.getBrand().getName(),
                        product.getBrand().getSubName(),
                        product.getBrand().getLogoUrl(),
                        product.getCategory(),
                        product.getLikeCount(),
                        product.isLikedByCurrentUser(),
                        product.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}

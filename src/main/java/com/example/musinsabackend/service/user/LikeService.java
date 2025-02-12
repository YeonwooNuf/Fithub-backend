package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.LikeDto;
import com.example.musinsabackend.model.Like;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.user.LikeRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository,
                       ProductRepository productRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Map<String, Object> toggleLike(LikeDto likeDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long userId = user.getUserId();
        Long productId = likeDto.getProductId();

        if (productId == null) {
            throw new IllegalArgumentException("상품 ID가 null입니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        boolean likedByCurrentUser;

        if (likeRepository.existsByUser_UserIdAndProduct_Id(userId, productId)) {
            likeRepository.deleteByUser_UserIdAndProduct_Id(userId, productId);
            product.decrementLikeCount();
            likedByCurrentUser = false; // ✅ 좋아요 취소됨
        } else {
            likeRepository.save(new Like(user, product));
            product.incrementLikeCount();
            likedByCurrentUser = true; // ✅ 좋아요 추가됨
        }

        productRepository.save(product);

        // ✅ 좋아요 상태와 좋아요 수 반환
        Map<String, Object> response = new HashMap<>();
        response.put("likedByCurrentUser", likedByCurrentUser);
        response.put("likeCount", product.getLikeCount());

        return response;
    }
}

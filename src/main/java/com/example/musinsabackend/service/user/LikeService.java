package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.LikeDto;
import com.example.musinsabackend.model.Like;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.user.LikeRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void toggleLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long productId = likeDto.getProductId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (likeRepository.existsByUser_UserIdAndProduct_Id(userId, productId)) {
            // ✅ 좋아요 취소
            likeRepository.deleteByUser_UserIdAndProduct_Id(userId, productId);
            product.decrementLikeCount(); // ✅ 좋아요 수 감소
        } else {
            // ✅ 좋아요 추가
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            likeRepository.save(new Like(user, product));
            product.incrementLikeCount(); // ✅ 좋아요 수 증가
        }

        productRepository.save(product); // ✅ 변경 사항 저장
    }
}
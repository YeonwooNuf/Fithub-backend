package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.CartItem;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ✅ 사용자별 장바구니 조회
    List<CartItem> findByUser_UserId(Long userId);

    // ✅ 동일한 상품 + 사이즈 + 색상 조합 찾기 (중복 방지)
    Optional<CartItem> findByUserAndProductAndSelectedSizeAndSelectedColor(
            User user, Product product, String selectedSize, String selectedColor
    );

    // ✅ 특정 사용자의 장바구니 전체 삭제
    void deleteByUser_UserId(Long userId);

    // ✅ 특정 사용자의 특정 상품 삭제
    void deleteByUser_UserIdAndProduct_Id(Long userId, Long productId);

    // ✅ 사용자별 장바구니 항목 수량 합계 조회
    int countByUser_UserId(Long userId);
}

package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /** 특정 사용자의 장바구니 목록 조회 */
    List<CartItem> findByUserId(Long userId);

    /** 특정 사용자의 장바구니에서 동일한 상품(같은 사이즈 & 색상)이 있는지 확인 */
    Optional<CartItem> findByUserIdAndProductIdAndSizeAndColor(Long userId, Long productId, String size, String color);

    /** 특정 장바구니 아이템 조회 */
    Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId);

    /** 특정 사용자의 장바구니 전체 삭제 */
    void deleteByUserId(Long userId);
}

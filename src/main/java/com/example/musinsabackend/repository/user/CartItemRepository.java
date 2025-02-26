package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /** 특정 사용자의 장바구니 목록 조회 */
    List<CartItem> findByUser_UserId(Long userId);  // ✅ userId 대신 user.userId

    /** 특정 사용자의 장바구니에서 동일한 상품(같은 사이즈 & 색상)이 있는지 확인 */
    Optional<CartItem> findByUser_UserIdAndProductIdAndSizeAndColor(Long userId, Long productId, String size, String color);

    /** 특정 장바구니 아이템 조회 */
    Optional<CartItem> findByIdAndUser_UserId(Long cartItemId, Long userId);

    /** 특정 사용자의 장바구니 전체 삭제 */
    void deleteByUser_UserId(Long userId);  // ✅ delete도 userId가 아니라 user.userId
}

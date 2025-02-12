package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.CartItemDto;
import com.example.musinsabackend.model.CartItem;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.user.CartItemRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartItemService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ✅ 1️⃣ 장바구니에 상품 추가
    public CartItemDto addToCart(Long userId, Long productId, String selectedSize, String selectedColor, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // ✅ 기존 장바구니에 동일한 상품, 사이즈, 색상 조합이 있는지 확인
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductAndSelectedSizeAndSelectedColor(
                user, product, selectedSize, selectedColor
        );

        if (existingItem.isPresent()) {
            // ✅ 이미 존재하면 수량 증가
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            return CartItemDto.fromEntity(item);
        } else {
            // ✅ 새로 추가
            CartItem newItem = new CartItem(user, product, selectedSize, selectedColor, quantity);
            cartItemRepository.save(newItem);
            return CartItemDto.fromEntity(newItem);
        }
    }

    // ✅ 2️⃣ 사용자별 장바구니 목록 조회
    public List<CartItemDto> getCartItemsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUser_UserId(userId);
        return cartItems.stream()
                .map(CartItemDto::fromEntity) // ✅ DTO로 변환
                .collect(Collectors.toList());
    }

    // ✅ 3️⃣ 장바구니 항목 수정 (수량 변경)
    public CartItemDto updateCartItem(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        cartItem.setQuantity(quantity); // ✅ 수량 업데이트
        cartItemRepository.save(cartItem);
        return CartItemDto.fromEntity(cartItem);
    }

    // ✅ 4️⃣ 장바구니 항목 삭제
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));
        cartItemRepository.delete(cartItem);
    }

    // ✅ 5️⃣ 장바구니 전체 비우기
    public void clearCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUser_UserId(userId);
        cartItemRepository.deleteAll(cartItems);
    }
}

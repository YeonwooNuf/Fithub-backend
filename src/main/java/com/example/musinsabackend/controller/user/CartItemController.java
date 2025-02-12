package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.CartItemDto;
import com.example.musinsabackend.service.user.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    // ✅ 1️⃣ 장바구니에 상품 추가
    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam String selectedSize,
            @RequestParam String selectedColor,
            @RequestParam int quantity
    ) {
        CartItemDto cartItem = cartItemService.addToCart(userId, productId, selectedSize, selectedColor, quantity);
        return ResponseEntity.ok(cartItem);
    }

    // ✅ 2️⃣ 사용자별 장바구니 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDto>> getCartItems(@PathVariable Long userId) {
        List<CartItemDto> cartItems = cartItemService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    // ✅ 3️⃣ 장바구니 항목 수정 (수량 변경)
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        CartItemDto updatedItem = cartItemService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(updatedItem);
    }

    // ✅ 4️⃣ 장바구니 항목 삭제
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Map<String, String>> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(Map.of("message", "장바구니 항목이 삭제되었습니다."));
    }

    // ✅ 5️⃣ 장바구니 전체 비우기
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        cartItemService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "장바구니가 비워졌습니다."));
    }
}
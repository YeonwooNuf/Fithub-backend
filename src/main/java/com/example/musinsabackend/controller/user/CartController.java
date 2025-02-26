package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.CartResponseDto;
import com.example.musinsabackend.service.user.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /** ✅ 장바구니 목록 조회 */
    @GetMapping
    public ResponseEntity<CartResponseDto> getCartItems(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId"); // ✅ request에서 userId 가져오기
        CartResponseDto cartResponse = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartResponse);
    }

    /** ✅ 장바구니에 상품 추가 */
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam String size,
            @RequestParam String color,
            @RequestParam int quantity) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addToCart(userId, productId, size, color, quantity);
        return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
    }

    /** ✅ 장바구니에서 특정 상품 삭제 */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(
            HttpServletRequest request,
            @PathVariable Long cartItemId) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok("장바구니에서 상품이 삭제되었습니다.");
    }

    /** ✅ 장바구니 전체 삭제 */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return ResponseEntity.ok("장바구니가 비워졌습니다.");
    }

    /** ✅ 장바구니 최종 결제 금액 계산 */
    @PostMapping("/calculate-total")
    public ResponseEntity<Integer> calculateTotalPrice(
            HttpServletRequest request,
            @RequestBody List<Long> selectedCoupons,
            @RequestParam int usedPoints) {
        Long userId = (Long) request.getAttribute("userId");
        int finalPrice = cartService.calculateTotalPrice(userId, selectedCoupons, usedPoints);
        return ResponseEntity.ok(finalPrice);
    }
}

package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.CartItemDto;
import com.example.musinsabackend.dto.CartResponseDto;
import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.model.CartItem;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.model.coupon.UserCoupon;
import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.coupon.CouponTarget;
import com.example.musinsabackend.repository.user.CartItemRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.user.CouponRepository;
import com.example.musinsabackend.repository.PointRepository;
import com.example.musinsabackend.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PointRepository pointRepository;
    private final PointService pointService;

    /** 장바구니에 상품 추가 */
    @Transactional
    public void addToCart(Long userId, Long productId, String size, String color, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 같은 상품(같은 색상, 사이즈)이 이미 장바구니에 있는지 확인
        CartItem cartItem = cartItemRepository.findByUser_UserIdAndProductIdAndSizeAndColor(userId, productId, size, color)
                .orElse(CartItem.builder()
                        .user(user)
                        .product(product)
                        .size(size)
                        .color(color)
                        .quantity(0) // 기존 상품이 없으면 0부터 시작
                        .build());

        // 기존 상품이 있다면 수량만 추가
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);
    }

    /** 장바구니 목록 조회 */
    @Transactional(readOnly = true)
    public CartResponseDto getCartItems(Long userId) {
        List<CartItemDto> cartItems = cartItemRepository.findByUser_UserId(userId).stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());

        int totalPrice = cartItems.stream()
                .mapToInt(item -> (int) (item.getPrice() * item.getQuantity()))
                .sum();

        // 사용 가능한 쿠폰 조회 (UserCoupon 참조)
        List<CouponDto> availableCoupons = getUserCoupons(userId);

        // 사용 가능한 포인트 조회
        PointDto pointDto = getUserPoints(userId, totalPrice);

        return CartResponseDto.builder()
                .cartItems(cartItems)
                .totalPrice(totalPrice)
                .availableCoupons(availableCoupons)
                .availablePoints(pointDto)
                .build();
    }

    /** 장바구니에서 상품 삭제 */
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndUser_UserId(cartItemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));
        cartItemRepository.delete(cartItem);
    }

    /** 장바구니 전체 삭제 */
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUser_UserId(userId);
    }

    /** 사용 가능한 쿠폰 목록 조회 (UserCoupon 활용) */
    private List<CouponDto> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = couponRepository.findUserCouponsByUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        return userCoupons.stream()
                .map(userCoupon -> CouponDto.fromEntity(userCoupon.getCoupon()))
                .filter(userCoupon ->
                        userCoupon.getExpiryDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }

    /** 사용 가능한 포인트 조회 */
    private PointDto getUserPoints(Long userId, int totalPrice) {
        int totalPoints = pointService.getUserPointBalance(userId);

        int maxUsablePoints = (int) (totalPrice * 0.1);
        return PointDto.builder()
                .amount(totalPoints)
                .userId(userId)
                .build();
    }

    /** 장바구니 최종 결제 금액 계산 (포인트 차감 X) */
    @Transactional
    public int calculateTotalPrice(Long userId, List<Long> selectedCoupons, int usedPoints) {
        List<CartItemDto> cartItems = getCartItems(userId).getCartItems();
        int totalPrice = cartItems.stream().mapToInt(item -> (int) (item.getPrice() * item.getQuantity())).sum();

        // 쿠폰 적용
        int couponDiscount = applyCoupons(selectedCoupons, cartItems);

        // 포인트 사용 가능 여부만 체크 (차감은 결제 시점에서)
        int maxUsablePoints = (int) (totalPrice * 0.1);
        int finalUsedPoints = Math.min(usedPoints, maxUsablePoints);

        return totalPrice - couponDiscount - finalUsedPoints;
    }

    /** 쿠폰 적용 */
    private int applyCoupons(List<Long> selectedCoupons, List<CartItemDto> cartItems) {
        int totalDiscount = 0;
        for (Long couponId : selectedCoupons) {
            UserCoupon userCoupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

            Coupon coupon = userCoupon.getCoupon(); // ✅ UserCoupon에서 Coupon 객체 가져오기

            for (CartItemDto cartItem : cartItems) {
                if (isCouponApplicable(cartItem, coupon)) {
                    int discount = (int) (cartItem.getPrice() * (coupon.getDiscount() / 100.0));
                    discount = Math.min(discount, coupon.getMaxDiscountAmount());
                    totalDiscount += discount;
                }
            }
        }
        return totalDiscount;
    }

    /** 쿠폰 적용 가능 여부 확인 */
    private boolean isCouponApplicable(CartItemDto cartItem, Coupon coupon) {
        if (coupon.getTarget() == CouponTarget.ALL_PRODUCTS) return true;
        if (coupon.getTarget() == CouponTarget.BRAND && coupon.getTargetValue().equals(cartItem.getBrandName()))
            return true;
        if (coupon.getTarget() == CouponTarget.CATEGORY && coupon.getTargetValue().equals(cartItem.getCategory().name()))
            return true; // ✅ ENUM이므로 .name()을 사용하여 String으로 변환

        return false;
    }

    /** ✅ 장바구니 상품 수량 업데이트 */
    @Transactional
    public void updateCartItemQuantity(Long userId, Long cartItemId, int newQuantity) {
        // ✅ 해당 장바구니 아이템 찾기
        CartItem cartItem = cartItemRepository.findByIdAndUser_UserId(cartItemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에서 해당 상품을 찾을 수 없습니다."));

        // ✅ 최소 수량 1 이상 유지
        if (newQuantity < 1) {
            throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
        }

        // ✅ 수량 업데이트
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
    }
}

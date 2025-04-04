package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.OrderDto;
import com.example.musinsabackend.dto.OrderItemDto;
import com.example.musinsabackend.dto.OrderRequestDto;
import com.example.musinsabackend.model.*;
import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.coupon.UserCoupon;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.*;
import com.example.musinsabackend.repository.user.AddressRepository;
import com.example.musinsabackend.repository.user.CouponRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public void saveOrder(OrderRequestDto dto, Long userId) {

        if (orderRepository.existsByPaymentId(dto.getPaymentId())) {
            throw new IllegalStateException("이미 저장된 결제입니다.");
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 배송지 조회
        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다."));

        // 주문 생성
        Order order = Order.builder()
                .paymentId(dto.getPaymentId())
                .totalAmount(dto.getTotalAmount())
                .finalAmount(dto.getFinalAmount())
                .usedPoints(dto.getUsedPoints())
                .orderDate(LocalDateTime.now())
                .user(user)
                .address(address)
                .build();

        // 주문 상품(OrderItem) 생성 및 연결
        for (OrderItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .order(order)
                    .build();

            order.addOrderItem(orderItem); // 편의 메서드로 양방향 연결
        }

        // 쿠폰 연결 (ManyToMany)
        if (dto.getUsedCouponIds() != null && !dto.getUsedCouponIds().isEmpty()) {
            List<UserCoupon> userCoupons = couponRepository.findAllById(dto.getUsedCouponIds());
            List<Coupon> coupons = userCoupons.stream()
                    .map(UserCoupon::getCoupon)
                    .toList();
            order.setUsedCoupons(userCoupons);
        }

        // 저장 (OrderItem도 cascade로 같이 저장됨)
        orderRepository.save(order);
    }

    public List<OrderDto> getOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findByUser_UserId(userId);

        return orders.stream().map(order -> {
            List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> {
                Product product = item.getProduct();
                boolean reviewExists = reviewRepository.existsByUserIdAndProductId(userId, product.getId());

                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setProductId(product.getId());
                itemDto.setProductName(product.getName());
                itemDto.setPrice(item.getPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setReviewWritten(reviewExists);

                return itemDto;
            }).toList();

            OrderDto dto = new OrderDto();
            dto.setOrderId(order.getId());
            dto.setPaymentId(order.getPaymentId());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setFinalAmount(order.getFinalAmount());
            dto.setUsedPoints(order.getUsedPoints());
            dto.setOrderDate(order.getOrderDate().toString());
            dto.setItems(itemDtos);

            return dto;
        }).toList();
    }
}

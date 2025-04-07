package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.*;
import com.example.musinsabackend.model.*;
import com.example.musinsabackend.model.coupon.UserCoupon;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.*;
import com.example.musinsabackend.repository.user.AddressRepository;
import com.example.musinsabackend.repository.user.CouponRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import com.example.musinsabackend.repository.user.UserCouponRepository;
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
    private final UserCouponRepository userCouponRepository;

    /**
     * ✅ 주문 저장 후 OrderDto 반환
     */
    @Transactional
    public OrderDto saveOrder(OrderRequestDto dto, Long userId) {
        if (orderRepository.existsByPaymentId(dto.getPaymentId())) {
            throw new IllegalStateException("이미 저장된 결제입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다."));

        Order order = Order.builder()
                .paymentId(dto.getPaymentId())
                .totalAmount(dto.getTotalAmount())
                .finalAmount(dto.getFinalAmount())
                .usedPoints(dto.getUsedPoints())
                .orderDate(LocalDateTime.now())
                .user(user)
                .address(address)
                .build();

        for (OrderItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .color(itemDto.getColor())
                    .size(itemDto.getSize())
                    .order(order)
                    .build();

            order.addOrderItem(orderItem);
        }

        // ✅ 쿠폰 연결 (중간 테이블 저장)
        if (dto.getUsedCouponIds() != null && !dto.getUsedCouponIds().isEmpty()) {
            List<UserCoupon> userCoupons = userCouponRepository.findAllByIdIn(dto.getUsedCouponIds());
            for (UserCoupon userCoupon : userCoupons) {
                order.addUsedCoupon(userCoupon);
            }
        }

        orderRepository.save(order);

        // ✅ 저장된 주문 상세 정보를 반환
        return getOrderDetail(order.getId(), userId);
    }

    public List<OrderDto> getOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findByUser_UserId(userId);

        return orders.stream().map(order -> {
            List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> {
                Product product = item.getProduct();

                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setProductId(product.getId());
                itemDto.setProductName(product.getName());
                itemDto.setPrice(item.getPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setReviewWritten(false);

                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    itemDto.setProductImage(product.getImages().get(0));
                } else {
                    itemDto.setProductImage("/uploads/cloth-images/default.jpg");
                }

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

    public OrderDto getOrderDetail(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (!order.getUser().getUserId().equals(userId)) {
            throw new SecurityException("해당 주문에 접근할 수 없습니다.");
        }

        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getId());
        dto.setPaymentId(order.getPaymentId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setUsedPoints(order.getUsedPoints());
        dto.setOrderDate(order.getOrderDate().toString());

        // ✅ 배송지 정보
        dto.setAddress(new AddressDto(order.getAddress()));

        // ✅ 사용한 쿠폰 정보
        List<UserCouponDto> userCouponDtos = order.getUsedCoupons().stream()
                .map(UserCouponDto::new)
                .toList();
        dto.setUsedCoupons(userCouponDtos);

        // ✅ 상품 정보
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDto d = new OrderItemDto();
                    d.setProductId(item.getProduct().getId());
                    d.setProductName(item.getProduct().getName());
                    d.setPrice(item.getPrice());
                    d.setQuantity(item.getQuantity());
                    return d;
                })
                .toList();
        dto.setItems(itemDtos);

        return dto;
    }
}

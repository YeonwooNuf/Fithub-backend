package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.OrderDto;
import com.example.musinsabackend.model.Order;
import com.example.musinsabackend.model.OrderStatus;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.OrderRepository;
import com.example.musinsabackend.repository.admin.AdminProductRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminProductRepository productRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 주문 생성
    public void createOrder(OrderDto orderDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Product product = productRepository.findById(orderDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(orderDto.getQuantity());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    // 사용자 주문 내역 조회
    public List<OrderDto> getUserOrders(String username) {
        List<Order> orders = orderRepository.findByUser_Username(username);

        return orders.stream().map(order -> {
            OrderDto dto = new OrderDto();
            dto.setId(order.getId());
            dto.setProductName(order.getProduct().getName());
            dto.setQuantity(order.getQuantity());
            dto.setOrderDate(order.getOrderDate().format(dateTimeFormatter));
            dto.setStatus(order.getStatus().name());
            return dto;
        }).collect(Collectors.toList());
    }

    // 주문 상태 변경
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.setStatus(status);
        orderRepository.save(order);
    }
}

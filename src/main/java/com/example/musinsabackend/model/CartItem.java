package com.example.musinsabackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id", "selected_size", "selected_color"})
}) // ✅ 동일 상품, 사이즈, 색상 조합은 중복 방지
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ 성능 최적화를 위해 LAZY 로딩 적용
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // ✅ 상품의 기존 정보 포함

    @Column(name = "selected_size", nullable = false)
    private String selectedSize; // ✅ 사용자가 선택한 사이즈

    @Column(name = "selected_color", nullable = false)
    private String selectedColor; // ✅ 사용자가 선택한 색상

    @Column(nullable = false)
    private int quantity; // ✅ 수량

    // ✅ 기본 생성자
    public CartItem() {}

    // ✅ 전체 필드 초기화 생성자
    public CartItem(User user, Product product, String selectedSize, String selectedColor, int quantity) {
        this.user = user;
        this.product = product;
        this.selectedSize = selectedSize;
        this.selectedColor = selectedColor;
        this.quantity = quantity;
    }

    // ✅ Getter & Setter
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
    public String getSelectedSize() { return selectedSize; }
    public String getSelectedColor() { return selectedColor; }
    public int getQuantity() { return quantity; }

    public void setUser(User user) { this.user = user; }
    public void setProduct(Product product) { this.product = product; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

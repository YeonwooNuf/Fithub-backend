package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID

    @Column(nullable = false)
    private String name; // 상품명

    @Column(nullable = false)
    private Double price; // 가격

    @Column(nullable = true)
    private String size; // 사이즈

    @Column(nullable = true)
    private String color; // 색상

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Order> orders; // 해당 상품의 주문 내역

    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

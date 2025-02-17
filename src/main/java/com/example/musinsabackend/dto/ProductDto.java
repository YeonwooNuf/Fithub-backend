package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.ProductCategory;
import java.util.List;

public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private List<String> images;
    private List<String> sizes;
    private List<String> colors;
    private String brandName;
    private String brandSubName;
    private String brandLogoUrl;
    private ProductCategory category;
    private int likeCount;
    private boolean likedByCurrentUser;

    public ProductDto(Long id, String name, Double price, String description,
                      List<String> images, List<String> sizes, List<String> colors,
                      String brandName, String brandSubName, String brandLogoUrl, ProductCategory category,
                      int likeCount, boolean likedByCurrentUser) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
        this.sizes = sizes;
        this.colors = colors;
        this.brandName = brandName;
        this.brandSubName = brandSubName;
        this.brandLogoUrl = brandLogoUrl;
        this.category = category;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    // ✅ Getter & Setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
    public List<String> getImages() { return images; }

    public void setImages(List<String> images) { this.images = images; }
    public List<String> getSizes() { return sizes; }

    public void setSizes(List<String> sizes) { this.sizes = sizes; }
    public List<String> getColors() { return colors; }

    public void setColors(List<String> colors) { this.colors = colors; }
    public String getBrandName() { return brandName; }

    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getBrandSubName() {
        return brandSubName;
    }

    public void setBrandSubName(String brandSubName) {
        this.brandSubName = brandSubName;
    }

    public String getBrandLogoUrl() { return brandLogoUrl; }
    public void setBrandLogoUrl(String brandLogoUrl) { this.brandLogoUrl = brandLogoUrl; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}

package com.canteenx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * JavaBean representing a Food Item in the CanteenX catalog.
 * Follows JavaBean standards: serializable, private fields, default constructor, getters/setters.
 */
public class FoodItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int itemId;
    private int categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
    private String image;
    private Timestamp createdAt;

    public FoodItem() {
        this.available = true;
        this.price = BigDecimal.ZERO;
        this.image = "assets/images/food-placeholder.jpg";
    }

    public FoodItem(int itemId, int categoryId, String name, String description, BigDecimal price, boolean available, String image) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.image = image;
    }

    public FoodItem(int itemId, int categoryId, String categoryName, String name, String description, BigDecimal price, boolean available, String image, Timestamp createdAt) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.image = image;
        this.createdAt = createdAt;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Alias for itemName for compatibility
    public String getItemName() {
        return name;
    }

    public void setItemName(String itemName) {
        this.name = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Alias for imageUrl
    public String getImageUrl() {
        return image;
    }

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Converts to MenuItem for interoperability.
     */
    public MenuItem toMenuItem() {
        MenuItem m = new MenuItem();
        m.setItemId(this.itemId);
        m.setCategoryId(this.categoryId);
        m.setCategoryName(this.categoryName);
        m.setItemName(this.name);
        m.setDescription(this.description);
        m.setPrice(this.price);
        m.setAvailable(this.available);
        m.setImageUrl(this.image);
        m.setCreatedAt(this.createdAt);
        return m;
    }

    public static FoodItem fromMenuItem(MenuItem m) {
        if (m == null) return null;
        return new FoodItem(
            m.getItemId(),
            m.getCategoryId(),
            m.getCategoryName(),
            m.getItemName(),
            m.getDescription(),
            m.getPrice(),
            m.isAvailable(),
            m.getImageUrl(),
            m.getCreatedAt()
        );
    }
}

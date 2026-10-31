package com.canteenx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * JavaBean representing a food item in the canteen catalog.
 */
public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int itemId;
    private int categoryId;
    private String categoryName;
    private String itemName;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;
    private String imageUrl;
    private Timestamp createdAt;

    public MenuItem() {
    }

    public MenuItem(int itemId, int categoryId, String categoryName, String itemName, 
                    String description, BigDecimal price, boolean isAvailable, 
                    String imageUrl, Timestamp createdAt) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getImageUrl() {
        String mapped = resolveDishImage(this.itemName, this.itemId);
        if (mapped != null) {
            return mapped;
        }
        if (this.imageUrl != null && !this.imageUrl.trim().isEmpty() && !this.imageUrl.contains("food-placeholder")) {
            return this.imageUrl;
        }
        return "assets/images/food-placeholder.jpg";
    }

    public String getRawImageUrl() {
        return this.imageUrl;
    }

    /**
     * Resolves the dish-specific local image asset for known menu items.
     * Maps based on normalized item name as well as item ID.
     */
    public static String resolveDishImage(String name, int id) {
        if (name != null) {
            String clean = name.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (clean.contains("idli") || clean.contains("vada")) {
                return "assets/images/idli-vada-combo.jpg";
            }
            if (clean.contains("dosa")) {
                return "assets/images/masala-dosa.jpg";
            }
            if (clean.contains("poha")) {
                return "assets/images/poha-with-sev.jpg";
            }
            if (clean.contains("chole") || clean.contains("bhature")) {
                return "assets/images/chole-bhature.jpg";
            }
            if (clean.contains("paneer") && clean.contains("rice")) {
                return "assets/images/paneer-fried-rice.jpg";
            }
            if (clean.contains("thali")) {
                return "assets/images/special-veg-thali.jpg";
            }
            if (clean.contains("samosa")) {
                return "assets/images/aloo-samosa.jpg";
            }
            if (clean.contains("sandwich") || clean.contains("cheese")) {
                return "assets/images/grilled-cheese-sandwich.jpg";
            }
            if (clean.contains("paneer") && clean.contains("roll")) {
                return "assets/images/paneer-tikka-roll.jpg";
            }
            if (clean.contains("spring") || clean.contains("roll")) {
                return "assets/images/veg-spring-roll.jpg";
            }
            if (clean.contains("coffee")) {
                return "assets/images/cold-coffee-ice-cream.jpg";
            }
            if (clean.contains("lime") || clean.contains("soda")) {
                return "assets/images/fresh-lime-soda.jpg";
            }
            if (clean.contains("chai") || clean.contains("tea") || clean.contains("kulhad")) {
                return "assets/images/kulhad-masala-chai.jpg";
            }
            if (clean.contains("brownie") || clean.contains("chocolate")) {
                return "assets/images/chocolate-brownie.jpg";
            }
            if (clean.contains("gulab") || clean.contains("jamun")) {
                return "assets/images/gulab-jamun.jpg";
            }
        }

        // Secondary fallback mapping by item ID
        switch (id) {
            case 1: return "assets/images/masala-dosa.jpg";
            case 2: return "assets/images/idli-vada-combo.jpg";
            case 3: return "assets/images/poha-with-sev.jpg";
            case 4: return "assets/images/special-veg-thali.jpg";
            case 5: return "assets/images/paneer-fried-rice.jpg";
            case 6: return "assets/images/chole-bhature.jpg";
            case 7: return "assets/images/aloo-samosa.jpg";
            case 8: return "assets/images/grilled-cheese-sandwich.jpg";
            case 9: return "assets/images/veg-spring-roll.jpg";
            case 10: return "assets/images/kulhad-masala-chai.jpg";
            case 11: return "assets/images/cold-coffee-ice-cream.jpg";
            case 12: return "assets/images/fresh-lime-soda.jpg";
            case 13: return "assets/images/gulab-jamun.jpg";
            case 14: return "assets/images/chocolate-brownie.jpg";
            case 15: return "assets/images/paneer-tikka-roll.jpg";
            default: return null;
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

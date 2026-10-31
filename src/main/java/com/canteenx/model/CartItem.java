package com.canteenx.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * JavaBean representing a single line item within a user's shopping cart.
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private MenuItem menuItem;
    private int quantity;

    public CartItem() {
    }

    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        if (menuItem != null && menuItem.getPrice() != null) {
            return menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}

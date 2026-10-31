package com.canteenx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JavaBean representing the student's active shopping cart.
 * Stored in HttpSession to manage cart state across HTTP requests.
 */
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, CartItem> items;

    public Cart() {
        this.items = new LinkedHashMap<>();
    }

    public synchronized void addItem(MenuItem item, int quantity) {
        if (item == null || quantity <= 0) {
            return;
        }
        int itemId = item.getItemId();
        if (items.containsKey(itemId)) {
            CartItem existing = items.get(itemId);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(itemId, new CartItem(item, quantity));
        }
    }

    public synchronized void updateQuantity(int itemId, int quantity) {
        if (items.containsKey(itemId)) {
            if (quantity <= 0) {
                items.remove(itemId);
            } else {
                items.get(itemId).setQuantity(quantity);
            }
        }
    }

    public synchronized void removeItem(int itemId) {
        items.remove(itemId);
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized Collection<CartItem> getItems() {
        return items.values();
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized int getTotalItemCount() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    public synchronized BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}

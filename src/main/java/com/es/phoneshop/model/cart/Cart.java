package com.es.phoneshop.model.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cart implements Serializable {
    private final CopyOnWriteArrayList<CartItem> items;
    private long totalQuantity;
    private BigDecimal totalCost;


    public Cart() {
        this.items = new CopyOnWriteArrayList<>();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public synchronized long getTotalQuantity() {
        return totalQuantity;
    }

    public synchronized void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public synchronized BigDecimal getTotalCost() {
        return totalCost;
    }

    public synchronized void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "Cart["  + items + "]";
    }
}

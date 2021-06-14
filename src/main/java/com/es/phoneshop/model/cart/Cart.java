package com.es.phoneshop.model.cart;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cart {
    private final CopyOnWriteArrayList<CartItem> items;

    public Cart() {
        this.items = new CopyOnWriteArrayList<>();
    }

    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Cart["  + items + "]";
    }
}

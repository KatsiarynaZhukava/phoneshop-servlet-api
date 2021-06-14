package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serializable;

public class CartItem implements Serializable {
    private final Product product;
    private long quantity;

    public CartItem( final Product product,
                     final long quantity ) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public synchronized long getQuantity() {
        return quantity;
    }

    public synchronized void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return product.getCode() + "," + quantity;
    }
}

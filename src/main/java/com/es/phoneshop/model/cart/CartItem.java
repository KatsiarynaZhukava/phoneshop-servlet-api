package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

public class CartItem {
    private final Product product;
    private final long quantity;

    public CartItem( final Product product,
                     final long quantity ) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return product.getCode() + "," + quantity;
    }
}

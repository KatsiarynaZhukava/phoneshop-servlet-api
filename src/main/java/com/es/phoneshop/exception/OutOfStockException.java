package com.es.phoneshop.exception;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends Exception {
    private final Product product;
    private final long stockRequested;
    private final long quantityOfItemsInCart;
    private final long stockAvailable;

    public OutOfStockException ( final Product product,
                                 final long stockRequested,
                                 final long stockAvailable,
                                 final long quantityOfItemsInCart ) {
        this.product = product;
        this.stockRequested = stockRequested;
        this.stockAvailable = stockAvailable;
        this.quantityOfItemsInCart = quantityOfItemsInCart;
    }

    public Product getProduct() {
        return product;
    }

    public long getStockRequested() {
        return stockRequested;
    }

    public long getStockAvailable() {
        return stockAvailable;
    }

    public long getQuantityOfItemsInCart() {
        return quantityOfItemsInCart;
    }
}

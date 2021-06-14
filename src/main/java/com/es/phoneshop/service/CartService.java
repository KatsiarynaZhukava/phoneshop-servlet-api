package com.es.phoneshop.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;

import javax.servlet.http.HttpSession;

public interface CartService {
    Cart getCart(HttpSession session);
    void add(Cart cart, Long productId, long quantity, HttpSession session) throws OutOfStockException;
    void update(Cart cart, Long productId, long quantity, HttpSession session) throws OutOfStockException;
}

package com.es.phoneshop.service;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface RecentlyViewedService {
    ConcurrentLinkedQueue<Product> getRecentlyViewed(HttpSession session);
    void add(ConcurrentLinkedQueue<Product> recentlyViewedProducts, Long productId, HttpSession session);
}

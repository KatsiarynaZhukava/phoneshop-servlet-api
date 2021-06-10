package com.es.phoneshop.service;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayDeque;

public interface RecentlyViewedService {
    ArrayDeque<Product> getRecentlyViewed(HttpServletRequest request);
    void add(ArrayDeque<Product> recentlyViewedProducts, Long productId);
}

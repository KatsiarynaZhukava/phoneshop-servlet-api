package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayDeque;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultRecentlyViewedService implements RecentlyViewedService {
    private final ProductDao productDao;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE = DefaultRecentlyViewedService.class.getName() + "recentlyViewed";
    private static final String PRODUCT_NOT_FOUND_BY_ID = "Product not found by id: {0}";

    private DefaultRecentlyViewedService() {
        productDao = ArrayListProductDao.getInstance();
    }

    private static class InstanceHolder {
        private static final DefaultRecentlyViewedService INSTANCE = new DefaultRecentlyViewedService();
    }

    public static DefaultRecentlyViewedService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public ArrayDeque<Product> getRecentlyViewed(final HttpServletRequest request) {
        lock.lock();
        try {
            ArrayDeque<Product> recentlyViewedProducts = (ArrayDeque<Product>) request.getSession().getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
            if (recentlyViewedProducts == null) {
                request.getSession().setAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE, recentlyViewedProducts = new ArrayDeque<>(3));
            }
            return recentlyViewedProducts;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(final ArrayDeque<Product> recentlyViewedProducts, final Long productId) {
        lock.lock();
        try {
            Product product = productDao.getProduct(productId)
                                        .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId));
            recentlyViewedProducts.remove(product);
            recentlyViewedProducts.addLast(product);
            if (recentlyViewedProducts.size() > 3) {
                recentlyViewedProducts.removeFirst();
            }
        } finally {
            lock.unlock();
        }
    }
}

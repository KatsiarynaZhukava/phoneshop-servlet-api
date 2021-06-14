package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.util.lock.DefaultSessionLockManager;
import com.es.phoneshop.util.lock.SessionLockManager;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID;

public class DefaultRecentlyViewedService implements RecentlyViewedService {
    private final ProductDao productDao;
    private static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE = DefaultRecentlyViewedService.class.getName() + "recentlyViewed";
    private static final Long RECENTLY_VIEWED_NUMBER_OF_ITEMS = 3L;
    private final SessionLockManager sessionLockManager;

    private DefaultRecentlyViewedService() {
        productDao = ArrayListProductDao.getInstance();
        sessionLockManager = new DefaultSessionLockManager();
    }

    private static class InstanceHolder {
        private static final DefaultRecentlyViewedService INSTANCE = new DefaultRecentlyViewedService();
    }

    public static DefaultRecentlyViewedService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public ConcurrentLinkedQueue<Product> getRecentlyViewed(final HttpSession session) {
        Lock lock = sessionLockManager.getSessionLock(session);
        lock.lock();
        try {
            ConcurrentLinkedQueue<Product> recentlyViewedProducts = (ConcurrentLinkedQueue<Product>) session.getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
            if (recentlyViewedProducts == null) {
                session.setAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE, recentlyViewedProducts = new ConcurrentLinkedQueue<>());
            }
            return recentlyViewedProducts;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(final ConcurrentLinkedQueue<Product> recentlyViewedProducts, final Long productId, final HttpSession session) {
        Lock lock = sessionLockManager.getSessionLock(session);
        lock.lock();
        try {
            Product product = productDao.getProduct(productId)
                                        .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId));
            recentlyViewedProducts.remove(product);
            recentlyViewedProducts.add(product);
            if (recentlyViewedProducts.size() > RECENTLY_VIEWED_NUMBER_OF_ITEMS) {
                recentlyViewedProducts.remove();
            }
        } finally {
            lock.unlock();
        }
    }
}
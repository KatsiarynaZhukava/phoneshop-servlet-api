package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.locks.ReentrantLock;

import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID;

public class DefaultCartService implements CartService {
    private final ProductDao productDao;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + "cart";

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    private static class InstanceHolder {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return DefaultCartService.InstanceHolder.INSTANCE;
    }

    @Override
    public Cart getCart(final HttpServletRequest request) {
        lock.lock();
        try {
            Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
            }
            return cart;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add (final Cart cart, final Long productId, long requestedQuantity) throws OutOfStockException {
        lock.lock();
        try {
            Product product = productDao.getProduct(productId)
                                        .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId));
            long quantityOfItemsInCart = cart.getItems().stream()
                                                        .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                                                        .map(CartItem::getQuantity)
                                                        .reduce(Long::sum)
                                                        .orElse(0L);
            if ( product.getStock() < (quantityOfItemsInCart + requestedQuantity) ) {
                throw new OutOfStockException(product, quantityOfItemsInCart + requestedQuantity, product.getStock(), quantityOfItemsInCart);
            }
            cart.getItems().add(new CartItem(product, requestedQuantity));
        } finally {
            lock.unlock();
        }
    }
}

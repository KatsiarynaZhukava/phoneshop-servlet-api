package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.util.lock.DefaultSessionLockManager;
import com.es.phoneshop.util.lock.SessionLockManager;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID;
import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID_IN_CART;

public class DefaultCartService implements CartService {
    private final ProductDao productDao;
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + "cart";
    private final SessionLockManager sessionLockManager;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
        sessionLockManager = new DefaultSessionLockManager();
    }

    private static class InstanceHolder {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return DefaultCartService.InstanceHolder.INSTANCE;
    }

    @Override
    public Cart getCart(final HttpSession session) {
        Lock lock = sessionLockManager.getSessionLock(session);
        lock.lock();
        try {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
            }
            return cart;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(final Cart cart, final Long productId, long requestedQuantity, final HttpSession session) throws OutOfStockException {
        Lock lock = sessionLockManager.getSessionLock(session);
        lock.lock();
        try {
            Product product = productDao.getProduct(productId)
                                        .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId));
            List<CartItem> cartItems = cart.getItems();
            long quantityOfItemsInCart = cartItems.stream()
                                                  .map(CartItem::getProduct)
                                                  .collect(Collectors.toList())
                                                  .contains(product) ?
                    cartItems.stream()
                             .filter(cartItem -> cartItem.getProduct().equals(product))
                             .findFirst()
                             .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID_IN_CART, productId))
                             .getQuantity()
                    : 0;
            if ( product.getStock() < (quantityOfItemsInCart + requestedQuantity ) ) {
                throw new OutOfStockException(product, quantityOfItemsInCart + requestedQuantity, product.getStock(), quantityOfItemsInCart);
            }
            cartItems.removeIf(cartItem -> cartItem.getProduct().equals(product));
            cartItems.add(new CartItem(product, requestedQuantity + quantityOfItemsInCart));
            recalculateCart(cart);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(final Cart cart, final Long productId, final long quantity, final HttpSession session) throws OutOfStockException {
        Lock lock = sessionLockManager.getSessionLock(session);
        lock.lock();
        try {
            Product product = productDao.getProduct(productId)
                                        .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId));
            if ( product.getStock() < (quantity) ) {
                throw new OutOfStockException(product, quantity, product.getStock(), 0);
            }
            List<CartItem> cartItems = cart.getItems();
            cartItems.stream()
                     .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                     .findFirst()
                     .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID_IN_CART, productId))
                     .setQuantity(quantity);
            recalculateCart(cart);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(Cart cart, Long productId, HttpSession session) {
        Lock lock = sessionLockManager.getSessionLock(session);
        lock.lock();
        try {
            cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));
            recalculateCart(cart);
        } finally {
            lock.unlock();
        }
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                                             .map(CartItem::getQuantity)
                                             .mapToLong(quantity -> quantity).sum());
        cart.setTotalCost(cart.getItems().stream()
                                         .map(cartItem -> cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                                         .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.util.DataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession httpSession;
    private CartService cartService;

    @Before
    public void setup() {
        DataManager.setUpProductDao();
        cartService = DefaultCartService.getInstance();
        when(request.getSession()).thenReturn(httpSession);
    }

    @Test
    public void testGetCartNotInSession() {
        Cart cart = cartService.getCart(request);
        assertNotEquals(null, cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testGetCartAlreadyInSession() {
        Cart cart;
        when(request.getSession().getAttribute(DefaultCartService.class.getName() + "cart")).thenReturn(cart = new Cart());
        Cart cartFromService = cartService.getCart(request);
        assertEquals(cart, cartFromService);
    }

    @Test(expected = NotFoundException.class)
    public void testAddToCartInvalidProductId() throws OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, -1L, 1L);
    }

    @Test
    public void testAddToCartCorrectQuantity() throws OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, 0L, 1L);
        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddToCartStockExceeded() throws OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, 0L, 101L);
    }

    @Test(expected = OutOfStockException.class)
    public void testAddToCartStockExceededWithItemsInCart() throws OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, 0L, 99L);
        assertEquals(1, cart.getItems().size());
        assertEquals(99, cart.getItems().get(0).getQuantity());
        cartService.add(cart, 0L, 2L);
        assertEquals(1, cart.getItems().size());
    }
}

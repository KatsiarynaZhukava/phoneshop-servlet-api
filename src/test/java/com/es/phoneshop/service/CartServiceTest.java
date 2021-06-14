package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.util.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {
    @Mock
    private HttpSession httpSession;
    private final CartService cartService = DefaultCartService.getInstance();

    @Before
    public void setup() {
        DataProvider.setUpProductDao();
    }

    @Test
    public void testGetCartNotInSession() {
        Cart cart = cartService.getCart(httpSession);
        assertNotEquals(null, cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testGetCartAlreadyInSession() {
        Cart cart;
        when(httpSession.getAttribute(DefaultCartService.class.getName() + "cart")).thenReturn(cart = new Cart());
        Cart cartFromService = cartService.getCart(httpSession);
        assertEquals(cart, cartFromService);
    }

    @Test(expected = NotFoundException.class)
    public void testAddToCartInvalidProductId() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, -1L, 1L, httpSession);
    }

    @Test
    public void testAddToCartCorrectQuantity() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 1L, httpSession);
        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddToCartStockExceeded() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 101L, httpSession);
    }

    @Test(expected = OutOfStockException.class)
    public void testAddToCartStockExceededWithItemsInCart() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 99L, httpSession);
        assertEquals(1, cart.getItems().size());
        assertEquals(99, cart.getItems().get(0).getQuantity());
        cartService.add(cart, 0L, 2L, httpSession);
        assertEquals(1, cart.getItems().size());
    }
}

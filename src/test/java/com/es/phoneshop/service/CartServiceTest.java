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
import java.math.BigDecimal;
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

    @Test
    public void testDeleteItem() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 1L, httpSession);
        cartService.add(cart, 1L, 2L, httpSession);
        cartService.add(cart, 2L, 3L, httpSession);
        long numberOfItems = cart.getItems().size();

        cartService.delete(cart, 1L, httpSession);
        assertEquals(numberOfItems - 1, cart.getItems().size());
        assertEquals(0L, cart.getItems().get(0).getProduct().getId().longValue() );
        assertEquals(1L, cart.getItems().get(0).getQuantity());
        assertEquals(2L, cart.getItems().get(1).getProduct().getId().longValue());
        assertEquals(3L, cart.getItems().get(1).getQuantity());
        assertEquals(4L, cart.getTotalQuantity());
        assertEquals(cart.getItems().get(0).getProduct().getPrice().multiply(new BigDecimal(cart.getItems().get(0).getQuantity())).add(
                     cart.getItems().get(1).getProduct().getPrice().multiply(new BigDecimal(cart.getItems().get(1).getQuantity()))),
                     cart.getTotalCost());
    }

    @Test
    public void testUpdateCart() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 1L, httpSession);
        cartService.add(cart, 1L, 2L, httpSession);

        cartService.update(cart, 0L, 2L, httpSession);
        assertEquals(2L, cart.getItems().get(0).getQuantity());
        assertEquals(2L + 2L, cart.getTotalQuantity());
        assertEquals(cart.getItems().get(0).getProduct().getPrice().multiply(new BigDecimal(cart.getItems().get(0).getQuantity())).add(
                     cart.getItems().get(1).getProduct().getPrice().multiply(new BigDecimal(cart.getItems().get(1).getQuantity()))),
                     cart.getTotalCost());
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateCartStockExceeded() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 1L, httpSession);
        cartService.update(cart, 0L, 1000L, httpSession);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateCartInvalidProductId() throws OutOfStockException {
        Cart cart = cartService.getCart(httpSession);
        cartService.add(cart, 0L, 1L, httpSession);
        cartService.update(cart, -1L, 2L, httpSession);
    }
}

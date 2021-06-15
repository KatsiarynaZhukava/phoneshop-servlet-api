package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DefaultCartService;
import com.es.phoneshop.util.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private HttpSession httpSession;

    private final CartService cartService = DefaultCartService.getInstance();
    private final CartPageServlet servlet = new CartPageServlet();
    private Cart cart;

    @Before
    public void setup() throws ServletException, OutOfStockException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        DataProvider.setUpProductDao();
        initializeCart();
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("cart"), any());
        verify(request).setAttribute(eq("recentlyViewedProducts"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithNonPositiveQuantity() throws ServletException, IOException {
        when(request.getParameterValues("productId")).thenReturn(new String[] {"0", "1"});
        when(request.getParameterValues("quantity")).thenReturn(new String[] {"-1", "1"});

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testDoPostWithQuantityNotANumber() throws ServletException, IOException, OutOfStockException {
        when(request.getParameterValues("productId")).thenReturn(new String[] {"0", "1"});
        when(request.getParameterValues("quantity")).thenReturn(new String[] {"aaa", "1"});

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testDoPostWithTooLargeQuantity() throws ServletException, IOException {
        when(request.getParameterValues("productId")).thenReturn(new String[] {"0", "1"});
        when(request.getParameterValues("quantity")).thenReturn(new String[] {"100000000000000000000000000000000", "1"});

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testDoPostStockExceeded() throws ServletException, IOException {

    }

    @Test
    public void testDoPostAddToCartSuccess() throws ServletException, IOException {

    }

    private void initializeCart() throws OutOfStockException {
        cart = new Cart();
        cartService.add(cart, 0L, 1L, httpSession);
        cartService.add(cart, 1L, 1L, httpSession);
        when(request.getSession().getAttribute(DefaultCartService.class.getName() + "cart")).thenReturn(cart);
        when(request.getSession()).thenReturn(httpSession);
    }
}
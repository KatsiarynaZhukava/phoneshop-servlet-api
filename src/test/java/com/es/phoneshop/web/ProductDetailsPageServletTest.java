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
public class ProductDetailsPageServletTest {
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

    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        DataProvider.setUpProductDao();
    }

    @Test
    public void testDoGetWithCorrectId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("product"), any());
        verify(request).setAttribute(eq("cart"), any());
        verify(request).setAttribute(eq("recentlyViewedProducts"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/product.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithIncorrectId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/-1");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("productId"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithIdNotANumber() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/aaa");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("productId"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithNonPositiveQuantity() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("-1");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
        verify(request).setAttribute(eq("productId"), any());
    }

    @Test
    public void testDoPostWithQuantityNotANumber() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("blabla");
        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), any());
        verify(request).setAttribute(eq("productId"), any());
    }

    @Test
    public void testDoPostWithTooLargeQuantity() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("10000000000000000000000000000000000000000");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
        verify(request).setAttribute(eq("productId"), any());      }

    @Test
    public void testDoPostWithInvalidProductId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/aaa");
        when(request.getParameter("productId")).thenReturn("aaa");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
        verify(request, times(2)).setAttribute(eq("productId"), any());
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostStockExceeded() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("10000");
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
        verify(request).setAttribute(eq("productId"), any());
    }

    @Test
    public void testDoPostStockExceededWithItemsInCart() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("100");

        Cart cart = new Cart();
        cartService.add(cart, 0L, 1L, httpSession);
        when(request.getSession().getAttribute(DefaultCartService.class.getName() + "cart")).thenReturn(cart);
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
        verify(request).setAttribute(eq("productId"), any());
    }

    @Test
    public void testDoPostAddToCartSuccess() throws ServletException, IOException {
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("1");
        servlet.doPost(request, response);
        verify(response, times(1)).sendRedirect(request.getRequestURI() + "?message=Added to cart successfully");
    }
}
package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
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
    private ProductDao productDao;
    @Mock
    private HttpSession httpSession;

    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        productDao = ArrayListProductDao.getInstance();
        productDao.save(new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(100), Currency.getInstance("USD"), 100, "https://github.com/andrewosipenko/phoneshop-ext-images/blob/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg?raw=true", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
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
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorProductNotFound.jsp");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithIdNotANumber() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/aaa");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("productId"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorProductNotFound.jsp");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithNonPositiveQuantity() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("quantity")).thenReturn("-1");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
    }

    @Test
    public void testDoPostWithQuantityNotANumber() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("quantity")).thenReturn("blabla");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
    }

    @Test
    public void testDoPostWithTooLargeQuantity() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("quantity")).thenReturn("10000000000000000000000000000000000000000");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
    }

    @Test
    public void testDoPostWithInvalidProductId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/aaa");
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
    }

    @Test
    public void testDoPostStockExceeded() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("quantity")).thenReturn("10000");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());
    }

    @Test
    public void testDoPostStockExceededWithItemsInCart() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getContextPath()).thenReturn("http://localhost:8080/phoneshop-servlet-api");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        verify(response, times(1)).sendRedirect(request.getContextPath() + "/products/0?message=Added to cart successfully");
    }
}
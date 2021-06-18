package com.es.phoneshop.web;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
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

    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        DataProvider.setUpProductDao();
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("products"), any());
        verify(request).setAttribute(eq("recentlyViewedProducts"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostAddToCartSuccess() throws ServletException, IOException {
        when(request.getParameter("productId")).thenReturn("0");
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
        servlet.doPost(request, response);
        response.sendRedirect(request.getRequestURI() + "?message=Added to cart successfully");
    }
}
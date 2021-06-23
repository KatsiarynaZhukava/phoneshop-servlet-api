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
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PriceHistoryPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;

    private final PriceHistoryPageServlet servlet = new PriceHistoryPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        ProductDao productDao = ArrayListProductDao.getInstance();
        productDao.save(new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(100), Currency.getInstance("USD"), 100, "https://github.com/andrewosipenko/phoneshop-ext-images/blob/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg?raw=true", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
    }

    @Test
    public void testDoGetWithCorrectId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/0");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("product"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/priceHistory.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithIncorrectId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/-1");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("message"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithIdNotANumber() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/aaa");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("message"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(requestDispatcher).forward(request, response);
    }
}
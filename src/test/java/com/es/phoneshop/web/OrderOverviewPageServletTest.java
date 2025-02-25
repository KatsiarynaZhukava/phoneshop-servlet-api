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
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;

    private final OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();
    ArrayList<String> secureIds;

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        secureIds = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            secureIds.add(UUID.randomUUID().toString());
        }
        DataProvider.setUpOrderDao(secureIds);
    }

    @Test
    public void testDoGetWithCorrectSecureId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + secureIds.get(0));
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithIncorrectSecureId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/blabla");
        servlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(request).setAttribute(eq("message"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp");
        verify(requestDispatcher).forward(request, response);
    }
}
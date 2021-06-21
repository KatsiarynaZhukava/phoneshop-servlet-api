package com.es.phoneshop.web;

import com.es.phoneshop.util.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private HttpSession httpSession;

    private final DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");
        when(request.getSession()).thenReturn(httpSession);
        DataProvider.setUpProductDao();
    }

    @Test
    public void testDoPostWithInvalidProductId() throws IOException {
        when(request.getPathInfo()).thenReturn("/-1");
        servlet.doPost(request, response);
        verify(response).sendRedirect(request.getContextPath() + "/cart?message=Cart item removed successfully");
    }

    @Test
    public void testDoPostSuccess() throws IOException {
        when(request.getPathInfo()).thenReturn("/0");
        servlet.doPost(request, response);
        verify(response).sendRedirect(request.getContextPath() + "/cart?message=Cart item removed successfully");
    }
}
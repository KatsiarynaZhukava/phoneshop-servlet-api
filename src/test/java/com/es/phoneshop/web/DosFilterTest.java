package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.web.filters.DosFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private FilterConfig filterConfig;

    private final Filter filter = new DosFilter();

    @Before
    public void setup() throws ServletException, OutOfStockException {
        filter.init(filterConfig);
        when(request.getRemoteAddr()).thenReturn("0.0.0.1");
    }

    @Test
    public void testAddNewIpAddress() throws ServletException, IOException {
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testTimeExceeded() throws ServletException, IOException, InterruptedException {
        filter.doFilter(request, response, chain);
        Thread.sleep(65000);
        filter.doFilter(request, response, chain);
        verify(chain, times(2)).doFilter(request, response);
    }

    @Test
    public void testRequestsNumberExceeded() throws ServletException, IOException {
        for(int i = 0; i <22; i++) {
            filter.doFilter(request, response, chain);
        }
        verify(chain, times(20)).doFilter(request, response);
        verify(response, times(2)).setStatus(429);
    }
}
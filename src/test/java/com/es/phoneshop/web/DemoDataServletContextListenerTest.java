package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ProductDao productDao;
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;

    DemoDataServletContextListener servletContextListener = new DemoDataServletContextListener();

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        if (!productDao.findProducts(null, null, null).isEmpty()) {
            productDao.clear();
        }
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void testInsertDemoDataTrue() {
        when(servletContextEvent.getServletContext().getInitParameter(eq("insertDemoData")))
                .thenReturn("true");
        servletContextListener.contextInitialized(servletContextEvent);
        assertEquals(12L, productDao.findProducts(null, null, null).size());
    }

    @Test
    public void testInsertDemoDataFalse() {
        when(servletContextEvent.getServletContext().getInitParameter(eq("insertDemoData")))
                .thenReturn("false");
        servletContextListener.contextInitialized(servletContextEvent);
        assertEquals(0, productDao.findProducts(null, null, null).size());
    }

    @Test
    public void testInsertDemoDataNonParseableToBoolean() {
        when(servletContextEvent.getServletContext().getInitParameter(eq("insertDemoData")))
                .thenReturn("someValue");
        servletContextListener.contextInitialized(servletContextEvent);
        assertEquals(0, productDao.findProducts(null, null, null).size());
    }
}



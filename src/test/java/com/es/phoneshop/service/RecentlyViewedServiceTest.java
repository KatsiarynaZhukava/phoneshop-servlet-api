package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.util.DataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayDeque;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecentlyViewedServiceTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession httpSession;
    private RecentlyViewedService recentlyViewedService;
    private ProductDao productDao;

    @Before
    public void setup() {
        DataManager.setUpProductDao();
        productDao = ArrayListProductDao.getInstance();
        recentlyViewedService = DefaultRecentlyViewedService.getInstance();
        when(request.getSession()).thenReturn(httpSession);
    }

    @Test
    public void testGetRecentlyViewedNotInSession() {
        ArrayDeque<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(request);
        assertNotEquals(null, recentlyViewedProducts);
        assertEquals(0, recentlyViewedProducts.size());
    }

    @Test
    public void testGetRecentlyViewedAlreadyInSession() {
        ArrayDeque<Product> recentlyViewedProducts;
        when(request.getSession().getAttribute(DefaultRecentlyViewedService.class.getName() + "recentlyViewed")).thenReturn(recentlyViewedProducts = new ArrayDeque<>());
        ArrayDeque<Product> recentlyViewedFromService = recentlyViewedService.getRecentlyViewed(request);
        assertEquals(recentlyViewedProducts, recentlyViewedFromService);
    }

    @Test(expected = NotFoundException.class)
    public void testAddToRecentlyViewedInvalidProductId() {
        ArrayDeque<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(request);
        recentlyViewedService.add(recentlyViewedProducts, -1L);
    }

    @Test
    public void testAddSameProductToRecentlyViewed() {
        ArrayDeque<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(request);
        recentlyViewedService.add(recentlyViewedProducts, 0L);
        assertEquals(1, recentlyViewedProducts.size());
        recentlyViewedService.add(recentlyViewedProducts, 0L);
        assertEquals(1, recentlyViewedProducts.size());
    }

    @Test
    public void testAddSameProductWithAnotherProduct() {
        ArrayDeque<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(request);
        recentlyViewedService.add(recentlyViewedProducts, 0L);
        assertEquals(1, recentlyViewedProducts.size());
        recentlyViewedService.add(recentlyViewedProducts, 1L);
        assertEquals(2, recentlyViewedProducts.size());
        recentlyViewedService.add(recentlyViewedProducts, 0L);
        assertEquals(2, recentlyViewedProducts.size());
        assertEquals(1L, recentlyViewedProducts.getFirst().getId().longValue());
        assertEquals(0L, recentlyViewedProducts.getLast().getId().longValue());
    }

    @Test
    public void testAddFourProductsToRecentlyViewed() {
        ArrayDeque<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(request);
        recentlyViewedService.add(recentlyViewedProducts, 0L);
        recentlyViewedService.add(recentlyViewedProducts, 1L);
        recentlyViewedService.add(recentlyViewedProducts, 2L);
        recentlyViewedService.add(recentlyViewedProducts, 3L);
        assertEquals(3, recentlyViewedProducts.size());
        assertEquals(1L, recentlyViewedProducts.getFirst().getId().longValue());
        assertTrue(recentlyViewedProducts.contains(productDao.getProduct(2L).get()));
        assertEquals(3L, recentlyViewedProducts.getLast().getId().longValue());
    }
}

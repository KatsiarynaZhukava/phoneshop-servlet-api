package com.es.phoneshop.service;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.util.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecentlyViewedServiceTest {
    @Mock
    private HttpSession httpSession;
    private final RecentlyViewedService recentlyViewedService = DefaultRecentlyViewedService.getInstance();

    @Before
    public void setup() {
        DataProvider.setUpProductDao();
    }

    @Test
    public void testGetRecentlyViewedNotInSession() {
        ConcurrentLinkedQueue<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(httpSession);
        assertNotEquals(null, recentlyViewedProducts);
        assertEquals(0, recentlyViewedProducts.size());
    }

    @Test
    public void testGetRecentlyViewedAlreadyInSession() {
        ConcurrentLinkedQueue<Product> recentlyViewedProducts;
        when(httpSession.getAttribute(DefaultRecentlyViewedService.class.getName() + "recentlyViewed")).thenReturn(recentlyViewedProducts = new ConcurrentLinkedQueue<>());
        ConcurrentLinkedQueue<Product> recentlyViewedFromService = recentlyViewedService.getRecentlyViewed(httpSession);
        assertEquals(recentlyViewedProducts, recentlyViewedFromService);
    }

    @Test(expected = NotFoundException.class)
    public void testAddToRecentlyViewedInvalidProductId() {
        ConcurrentLinkedQueue<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(httpSession);
        recentlyViewedService.add(recentlyViewedProducts, -1L, httpSession);
    }

    @Test
    public void testAddSameProductToRecentlyViewed() {
        ConcurrentLinkedQueue<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(httpSession);
        recentlyViewedService.add(recentlyViewedProducts, 0L, httpSession);
        assertEquals(1, recentlyViewedProducts.size());
        recentlyViewedService.add(recentlyViewedProducts, 0L, httpSession);
        assertEquals(1, recentlyViewedProducts.size());
    }

    @Test
    public void testAddSameProductWithAnotherProduct() {
        ConcurrentLinkedQueue<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(httpSession);
        recentlyViewedService.add(recentlyViewedProducts, 0L, httpSession);
        assertEquals(1, recentlyViewedProducts.size());
        recentlyViewedService.add(recentlyViewedProducts, 1L, httpSession);
        assertEquals(2, recentlyViewedProducts.size());
        recentlyViewedService.add(recentlyViewedProducts, 0L, httpSession);
        assertEquals(2, recentlyViewedProducts.size());
        assertEquals(1L, recentlyViewedProducts.element().getId().longValue());
        recentlyViewedProducts.remove();
        assertEquals(0L, recentlyViewedProducts.element().getId().longValue());
    }

    @Test
    public void testAddFourProductsToRecentlyViewed() {
        ConcurrentLinkedQueue<Product> recentlyViewedProducts = recentlyViewedService.getRecentlyViewed(httpSession);
        recentlyViewedService.add(recentlyViewedProducts, 0L, httpSession);
        recentlyViewedService.add(recentlyViewedProducts, 1L, httpSession);
        recentlyViewedService.add(recentlyViewedProducts, 2L, httpSession);
        recentlyViewedService.add(recentlyViewedProducts, 3L, httpSession);
        assertEquals(3, recentlyViewedProducts.size());
        assertEquals(1L, recentlyViewedProducts.element().getId().longValue());
        recentlyViewedProducts.remove();
        assertEquals(2L, recentlyViewedProducts.element().getId().longValue());
        recentlyViewedProducts.remove();
        assertEquals(3L, recentlyViewedProducts.element().getId().longValue());
    }
}

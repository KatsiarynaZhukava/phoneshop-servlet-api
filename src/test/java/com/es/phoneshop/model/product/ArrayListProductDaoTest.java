package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.util.DataManager;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        DataManager.setUpProductDao();
        productDao = ArrayListProductDao.getInstance();
    }

    @Test
    public void testFindExistingProductById() {
        Long id = 0L;
        Optional<Product> product = productDao.getProduct(id);
        assertTrue(product.isPresent());
    }

    @Test
    public void testFindNonexistentProductById() {
        Long id = -1L;
        assertFalse(productDao.getProduct(id).isPresent());
    }

    @Test
    public void testFindProducts() {
        List<Product> products = productDao.findProducts(null, null, null);
        assertFalse(products.isEmpty());
        assertTrue(products.stream().allMatch(product -> product.getPrice() != null));
        assertTrue(products.stream().allMatch(product -> product.getStock() > 0));
    }

    @Test
    public void testFindProductsWithOneWordQuery() {
        List<Product> products = productDao.findProducts("iphone", null, null);
        assertEquals(2, products.size());
        assertEquals("Apple iPhone", products.get(0).getDescription());
        assertEquals("Apple iPhone 6", products.get(1).getDescription());
    }

    @Test
    public void testFindProductsWithComplexQuery() {
        List<Product> products = productDao.findProducts("iphone 6", null, null);
        assertEquals(2, products.size());
        assertEquals("Apple iPhone 6", products.get(0).getDescription());
        assertEquals("Apple iPhone", products.get(1).getDescription());
    }

    @Test
    public void testFindProductsWithSortField() {
        List<Product> products = productDao.findProducts(null, SortField.PRICE, SortOrder.ASC);
        assertEquals("sgs", products.get(0).getCode());
        assertEquals("sgs2", products.get(1).getCode());
        assertEquals("iphone", products.get(2).getCode());
        assertEquals("sgs3", products.get(3).getCode());
        assertEquals("iphone6", products.get(4).getCode());
    }

    @Test
    public void testFindProductsWithSortOrder() {
        List<Product> products = productDao.findProducts(null, SortField.DESCRIPTION, SortOrder.DESC);
        assertEquals("sgs3", products.get(0).getCode());
        assertEquals("sgs2", products.get(1).getCode());
        assertEquals("sgs", products.get(2).getCode());
        assertEquals("iphone6", products.get(3).getCode());
        assertEquals("iphone", products.get(4).getCode());
    }

    @Test
    public void testRemoveExistingProduct() {
        Long id = 0L;
        productDao.delete(id);
        assertFalse(productDao.getProduct(id).isPresent());
    }

    @Test
    public void testSaveProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(null, "product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} );
        productDao.save(product);

        assertTrue(product.getId() > 0);
        Product result = productDao.getProduct(product.getId()).get();
        assertNotNull(result);
        assertEquals("product", result.getCode());
        assertEquals("Samsung Galaxy S", result.getDescription());
        assertEquals(new BigDecimal(100), result.getPrice());
        assertEquals(usd, result.getCurrency());
        assertEquals(100, result.getStock());
        assertEquals("https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", result.getImageUrl());
    }

    @Test
    public void testSaveSameProductTwiceWithoutId() {
        int initialSize, sizeAfterFirstSave, sizeAfterSecondSave;
        initialSize = productDao.findProducts(null, null, null).size();
        Product product = new Product(null, "product", "Palm Pixi", new BigDecimal(170), Currency.getInstance("USD"), 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30,10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} );
        productDao.save(product);
        sizeAfterFirstSave = productDao.findProducts(null, null, null).size();
        productDao.save(product);
        sizeAfterSecondSave = productDao.findProducts(null, null, null).size();
        assertTrue(productDao.findProducts(null, null, null).contains(product));
        assertEquals(sizeAfterFirstSave, initialSize + 1);
        assertEquals(sizeAfterFirstSave, sizeAfterSecondSave);
    }

    @Test(expected = NotFoundException.class)
    public void testSaveProductWithNonexistentId() {
        Product product = new Product(228L,"product", "Palm Pixi", new BigDecimal(170), Currency.getInstance("USD"), 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} );
        productDao.save(product);
    }

    @Test
    public void testSaveProductWithExistingId() {
        Long code = 1L;
        Product product = new Product(code, "product", "Palm Pixi", new BigDecimal(170), Currency.getInstance("USD"), 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} );
        productDao.save(product);
        assertTrue(productDao.findProducts(null, null, null).contains(product));
        assertEquals("product", productDao.findProducts(null, null, null).get(code.intValue()).getCode());
    }

    @Test
    public void testCreatePriceHistory() {
        Product product = new Product(null, "product", "Palm Pixi", new BigDecimal(170), Currency.getInstance("USD"), 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", null);
        productDao.save(product);
        productDao.createPriceHistory(product);
        assertNotNull(productDao.getProduct(product.getId()).get().getPriceHistory());
        assertFalse(productDao.getProduct(product.getId()).get().getPriceHistory().isEmpty());
        assertTrue(productDao.getProduct(product.getId()).get().getPriceHistory().containsValue(product.getPrice()));
    }

    @Test
    public void testAddRecordToNullPriceHistory() {
        Product product = new Product(null, "product", "Palm Pixi", new BigDecimal(170), Currency.getInstance("USD"), 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", null);
        productDao.save(product);
        BigDecimal price = new BigDecimal(200);
        productDao.addRecordToPriceHistory(product, LocalDateTime.now(), new BigDecimal(200));
        assertTrue(productDao.getProduct(product.getId()).get().getPriceHistory().containsValue(price));
    }

    @Test
    public void testAddRecordToNotNullPriceHistory() {
        Product product = new Product(null, "product", "Palm Pixi", new BigDecimal(170), Currency.getInstance("USD"), 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<>());
        productDao.save(product);
        BigDecimal price = new BigDecimal(200);
        productDao.addRecordToPriceHistory(product, LocalDateTime.now(), price);
        assertTrue(productDao.getProduct(product.getId()).get().getPriceHistory().containsValue(price));
    }
}

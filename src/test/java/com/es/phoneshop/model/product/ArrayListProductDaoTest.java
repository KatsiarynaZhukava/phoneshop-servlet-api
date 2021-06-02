package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        if (!productDao.findProducts(null, null, null).isEmpty()) {
            productDao.clear();
        }
        productDao.save(new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://github.com/andrewosipenko/phoneshop-ext-images/blob/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg?raw=true", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} ));
        productDao.save(new Product(null, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} ));
        productDao.save(new Product(null, "sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} ));
        productDao.save(new Product(null, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} ));
        productDao.save(new Product(null, "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} ));
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
    }

    @Test
    public void testFindProductsWithComplexQuery() {
        List<Product> products = productDao.findProducts("iphone 6", null, null);
        assertEquals(2, products.size());
        assertEquals("Apple iPhone 6", products.get(0).getDescription());
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
        Product product = new Product(null, "product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} );
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
        Currency usd = Currency.getInstance("USD");
        initialSize = productDao.findProducts(null, null, null).size();
        Product product = new Product(null, "product", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} );
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
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(228L,"product", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} );
        productDao.save(product);
    }

    @Test
    public void testSaveProductWithExistingId() {
        Currency usd = Currency.getInstance("USD");
        Long code = 1L;
        Product product = new Product(code, "product", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDate, BigDecimal>(){{ put(LocalDate.of(2021, Calendar.JUNE, 30), new BigDecimal(200)); put(LocalDate.now(), new BigDecimal(100)); }} );
        productDao.save(product);
        assertTrue(productDao.findProducts(null, null, null).contains(product));
        assertEquals("product", productDao.findProducts(null, null, null).get(code.intValue()).getCode());
    }
}

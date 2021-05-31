package com.es.phoneshop.model.product;

import org.junit.Before;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        //productDao = ArrayListProductDao().;
    }

   /* @Test
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
        List<Product> products = productDao.findProducts();
        assertFalse(products.isEmpty());
        assertTrue(products.stream().allMatch(product -> product.getPrice() != null));
        assertTrue(products.stream().allMatch(product -> product.getStock() > 0));
    }

    @Test
    public void testSaveProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
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
        initialSize = productDao.findProducts().size();
        Product product = new Product("product", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg");
        productDao.save(product);
        sizeAfterFirstSave = productDao.findProducts().size();
        productDao.save(product);
        sizeAfterSecondSave = productDao.findProducts().size();
        assertTrue(productDao.findProducts().contains(product));
        assertEquals(sizeAfterFirstSave, initialSize + 1);
        assertEquals(sizeAfterFirstSave, sizeAfterSecondSave);
    }

    @Test(expected = NotFoundException.class)
    public void testSaveProductWithNonexistentId() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(228L,"product", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg");
        productDao.save(product);
    }

    @Test
    public void testSaveProductWithExistingId() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(0L, "product", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg");
        productDao.save(product);
        assertTrue(productDao.findProducts().contains(product));
        assertEquals("product", productDao.findProducts().get(0).getCode());
    }

    @Test
    public void testDeleteExistingProduct() {
        Long id = 0L;
        productDao.delete(id);
        assertFalse(productDao.getProduct(id).isPresent());
    }*/
}

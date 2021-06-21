package com.es.phoneshop.util;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class DataProvider {
    public static void setUpProductDao() {
        ProductDao productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        if (!productDao.findProducts(null, null, null).isEmpty()) {
            productDao.clear();
        }

        productDao.save(new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://github.com/andrewosipenko/phoneshop-ext-images/blob/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg?raw=true", null));
        productDao.save(new Product(null, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", null));
        productDao.save(new Product(null, "sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", null));
        productDao.save(new Product(null, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", null));
        productDao.save(new Product(null, "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg", null));

        for (Product product: productDao.findProducts(null, null, null)) {
            HashMap<LocalDateTime, BigDecimal> priceHistory = new HashMap<LocalDateTime, BigDecimal>();
            priceHistory.put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200));
            priceHistory.put(LocalDateTime.now(), new BigDecimal(100));
            product.setPriceHistory(priceHistory);
        }
    }
}

package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class DemoDataServletContextListener implements ServletContextListener {
    private final ProductDao productDao;

    public DemoDataServletContextListener() {
        this.productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (Boolean.parseBoolean(event.getServletContext().getInitParameter("insertDemoData"))) {
            getSampleProducts().forEach(productDao::save);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    private List<Product> getSampleProducts() {
        List<Product> result = new ArrayList<>();
        Currency usd = Currency.getInstance("USD");
        result.add(new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://github.com/andrewosipenko/phoneshop-ext-images/blob/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg?raw=true", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        result.add(new Product(null, "simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", new HashMap<LocalDateTime, BigDecimal>(){{ put(LocalDateTime.of(2021, Calendar.JUNE, 30, 10, 2, 3), new BigDecimal(200)); put(LocalDateTime.now(), new BigDecimal(100)); }} ));
        return result;
    }
}

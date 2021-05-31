package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;

public class Product {
    private Long id;
    private String code;
    private String description;
    /** null means there is no price because the product is outdated or new */
    private BigDecimal price;
    /** can be null if the price is null */
    private Currency currency;
    private int stock;
    private String imageUrl;
    private HashMap<LocalDate, BigDecimal> priceHistory;

    public Product() {
    }

    public Product( final Long id,
                    final String code,
                    final String description,
                    final BigDecimal price,
                    final Currency currency,
                    final int stock,
                    final String imageUrl,
                    final HashMap<LocalDate, BigDecimal> priceHistory ) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.priceHistory = priceHistory;
    }

    public Product( final String code,
                    final String description,
                    final BigDecimal price,
                    final Currency currency,
                    final int stock,
                    final String imageUrl,
                    final HashMap<LocalDate, BigDecimal> priceHistory ) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.priceHistory = priceHistory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HashMap<LocalDate, BigDecimal> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(HashMap<LocalDate, BigDecimal> priceHistory) {
        this.priceHistory = priceHistory;
    }
}
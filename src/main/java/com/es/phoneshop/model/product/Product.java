package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private HashMap<LocalDateTime, BigDecimal> priceHistory;

    public Product() {
    }

    public Product( final Long id,
                    final String code,
                    final String description,
                    final BigDecimal price,
                    final Currency currency,
                    final int stock,
                    final String imageUrl,
                    final HashMap<LocalDateTime, BigDecimal> priceHistory ) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public HashMap<LocalDateTime, BigDecimal> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(HashMap<LocalDateTime, BigDecimal> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public void addToPriceHistory(LocalDateTime key, BigDecimal value) {
        if (this.priceHistory == null) {
           priceHistory = new HashMap<>();
        }
        priceHistory.put(key, value);
    }
}
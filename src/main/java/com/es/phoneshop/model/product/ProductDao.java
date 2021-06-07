package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getProduct(Long id);
    List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);
    void save(Product product);
    void delete(Long id);
    void clear();
    void createPriceHistory(Product product);
    void addRecordToPriceHistory(Product product, LocalDateTime date, BigDecimal price);
}

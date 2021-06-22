package com.es.phoneshop.model.order;

import java.util.Optional;

public interface OrderDao {
    Optional<Order> getOrder(Long id);
    void save(Order order);
    Optional<Order> getOrderBySecureId(String secureId);
    void clear();
}

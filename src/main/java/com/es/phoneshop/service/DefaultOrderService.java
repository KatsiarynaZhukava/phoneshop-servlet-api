package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.PaymentMethod;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    private OrderDao orderDao;

    private DefaultOrderService() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    private static class InstanceHolder {
        private static final DefaultOrderService INSTANCE = new DefaultOrderService();
    }

    public static DefaultOrderService getInstance() {
        return DefaultOrderService.InstanceHolder.INSTANCE;
    }

    @Override
    public Order getOrder(final Cart cart) {
        Order order = new Order();
        order.setItems(cart.getItems().stream().map(item -> {
            try {
                return (CartItem) item.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toCollection(CopyOnWriteArrayList::new)));
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(final Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(5);
    }
}

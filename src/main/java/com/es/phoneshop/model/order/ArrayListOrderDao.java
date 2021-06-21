package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.es.phoneshop.util.Messages.ORDER_NOT_FOUND_BY_ID;

public class ArrayListOrderDao implements OrderDao {
    public long maxId;
    private List<Order> orders;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ArrayListOrderDao() {
        orders = new ArrayList<>();
    }

    private static class InstanceHolder {
        private static final OrderDao INSTANCE = new ArrayListOrderDao();
    }
    public static OrderDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        lock.readLock().lock();
        try {
            return orders.stream()
                         .filter(order -> id.equals(order.getId()))
                         .findAny();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Order> getOrderBySecureId(String secureId) {
        lock.readLock().lock();
        try {
            return orders.stream()
                         .filter(order -> secureId.equals(order.getSecureId()))
                         .findAny();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Order order) {
        lock.writeLock().lock();
        try {
            if (order.getId() == null) {
                order.setId(maxId++);
                orders.add(order);
            } else {
                Optional<Order> foundOrder = getOrder(order.getId());
                if (foundOrder.isPresent()) {
                    int index = orders.indexOf(foundOrder.get());
                    orders.set(index, order);
                } else {
                    throw new NotFoundException(ORDER_NOT_FOUND_BY_ID, order.getId());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
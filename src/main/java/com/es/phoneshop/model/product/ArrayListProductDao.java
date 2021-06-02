package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static ProductDao instance;
    public long maxId;
    private List<Product> products;
    private final ReentrantReadWriteLock lock;
    private static final String PRODUCT_NOT_FOUND_BY_ID = "Product not found by id: {0}";

    private ArrayListProductDao() {
        lock = new ReentrantReadWriteLock();
        products = new ArrayList<>();
    }

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        lock.readLock().lock();
        try {
            return products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            List<Product> result;
            products = products.stream()
                               .filter(product -> product.getPrice() != null)
                               .filter(product -> product.getStock() > 0)
                               .collect(Collectors.toList());

            if (query == null || query.isEmpty()) {
                result = products;
            } else {
                String[] keywords = query.toLowerCase().split("[ ]+");
                ToIntFunction<Product> getNumberOfMatches = product ->
                        (int) Arrays.stream(keywords)
                                    .filter(product.getDescription().toLowerCase()::contains)
                                    .count();
                result = products.stream()
                                 .filter(product -> (getNumberOfMatches.applyAsInt(product)) > 0)
                                 .sorted(Comparator.comparingInt(getNumberOfMatches).reversed())
                                 .collect(Collectors.toList());
            }
            if (sortField != null) {
                Comparator<Product> comparator = Comparator.comparing(product -> {
                    if (SortField.DESCRIPTION == sortField) {
                        return (Comparable) product.getDescription();
                    } else {
                        return (Comparable) product.getPrice();
                    }
                });
                comparator = SortOrder.DESC == sortOrder ? comparator.reversed() : comparator;
                result = result.stream()
                        .sorted(comparator)
                        .collect(Collectors.toList());
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Product product) {
        lock.writeLock().lock();
        try {
            if (product.getId() == null) {
                product.setId(maxId++);
                products.add(product);
            } else {
                Optional<Product> foundProduct = getProduct(product.getId());
                if (foundProduct.isPresent()) {
                    int index = products.indexOf(foundProduct.get());
                    products.set(index, product);
                } else {
                    throw new NotFoundException(PRODUCT_NOT_FOUND_BY_ID, product.getId());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            products.removeIf(product ->
                    product.getId().equals(id)
            );
        } finally {
            lock.writeLock().unlock();
        }
    }
}
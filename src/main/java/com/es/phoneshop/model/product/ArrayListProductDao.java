package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID;

public class ArrayListProductDao implements ProductDao {
    public long maxId;
    private List<Product> products;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    private ArrayListProductDao() {
        products = new ArrayList<>();
    }

    private static class InstanceHolder {
        private static final ProductDao INSTANCE = new ArrayListProductDao();
    }

    public static ProductDao getInstance() {
        return InstanceHolder.INSTANCE;
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
                ToLongFunction<Product> getNumberOfMatches = product ->
                        (int) Arrays.stream(keywords)
                                    .filter(product.getDescription().toLowerCase()::contains)
                                    .count();
                result = products.stream()
                                 .filter(product -> (getNumberOfMatches.applyAsLong(product)) > 0)
                                 .sorted(Comparator.comparingLong(getNumberOfMatches).reversed())
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

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            products.clear();
            maxId = 0L;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void createPriceHistory(Product product) {
        lock.writeLock().lock();
        try {
            product.setPriceHistory(new HashMap<LocalDateTime, BigDecimal>(){{
                put(LocalDateTime.now(), product.getPrice());
            }});
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addRecordToPriceHistory(Product product, LocalDateTime date, BigDecimal price) {
        lock.writeLock().lock();
        try {
            product.addToPriceHistory(date, price);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
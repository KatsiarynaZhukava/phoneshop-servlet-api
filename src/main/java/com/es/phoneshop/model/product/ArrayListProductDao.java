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
    public List<Product> findProductsAdvanced(Map<SearchFields, String> searchParameters) {
        String descriptionType = getValue(searchParameters.get(SearchFields.DESCRIPTION_TYPE), "");
        String description = getValue(searchParameters.get(SearchFields.DESCRIPTION), "");
        String minPriceString = getValue(searchParameters.get(SearchFields.MIN_PRICE), "");
        String maxPriceString = getValue(searchParameters.get(SearchFields.MAX_PRICE), "");

        long minPrice;
        long maxPrice;
        if (!minPriceString.isEmpty()) {
            minPrice = Long.parseLong(minPriceString);
        } else {
            minPrice = 0;
        }
        if (!maxPriceString.isEmpty()) {
            maxPrice = Long.parseLong(maxPriceString);
        } else {
            maxPrice = Long.MAX_VALUE;
        }

        lock.readLock().lock();
        try {
            List<Product> result;
            if (description == null || description.isEmpty()) {
                result = products;
            } else {
                String[] keywords = description.toLowerCase().split("[ ]+");
                ToLongFunction<Product> getNumberOfMatches = product ->
                                                    (int) Arrays.stream(keywords)
                                                                .filter(product.getDescription().toLowerCase()::contains)
                                                                .count();
                if (descriptionType.equals("ANY_WORD")) {
                    result = products.stream()
                                     .filter(product -> (getNumberOfMatches.applyAsLong(product)) > 0)
                                     .sorted(Comparator.comparingLong(getNumberOfMatches).reversed())
                                     .collect(Collectors.toList());
                } else {
                    result = products.stream()
                                     .filter(product -> (getNumberOfMatches.applyAsLong(product)) == keywords.length)
                                     .collect(Collectors.toList());
                }
                long finalMinPrice = minPrice;
                long finalMaxPrice = maxPrice;
                result = result.stream()
                               .filter(product -> product.getPrice().longValue() >= finalMinPrice && product.getPrice().longValue() <= finalMaxPrice)
                               .collect(Collectors.toList());
            }
            return result;
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
            return sort(result, sortField, sortOrder);
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
            products.removeIf(product -> product.getId().equals(id));
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

    private List<Product> sort(List<Product> products, SortField sortField, SortOrder sortOrder) {
        if (sortField != null) {
            Comparator<Product> comparator = Comparator.comparing(product -> {
                if (SortField.DESCRIPTION == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) product.getPrice();
                }
            });
            comparator = SortOrder.DESC == sortOrder ? comparator.reversed() : comparator;
            return products.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            return products;
        }
    }

    private <T> T getValue(T primaryValue, T defaultValue) {
        return primaryValue == null ? defaultValue : primaryValue;
    }
}
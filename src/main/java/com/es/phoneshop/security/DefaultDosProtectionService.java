package com.es.phoneshop.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final long THRESHOLD = 20;
    private final Map<String, RequestsNumberHolder> countMap = new ConcurrentHashMap<>();
    private final Map<String, Lock> lockMap = new ConcurrentHashMap<>();

    private static class InstanceHolder {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    public static DefaultDosProtectionService getInstance() {
        return DefaultDosProtectionService.InstanceHolder.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip) {
        Lock lock = lockMap.get(ip);
        if (lock == null) {
            synchronized (this) {
                if (lock == null) {
                    lockMap.put(ip, lock = new ReentrantLock());
                }
            }
        }
        lock.lock();
        try {
            RequestsNumberHolder requestsNumberHolder = countMap.get(ip);
            if (requestsNumberHolder == null) {
                countMap.put(ip, new RequestsNumberHolder());
            } else {
                Long now = System.currentTimeMillis();
                if (TimeUnit.MILLISECONDS.toMinutes(now - requestsNumberHolder.getStartTime()) < 1) {
                    if (requestsNumberHolder.getCount() <= THRESHOLD) {
                        requestsNumberHolder.setCount(requestsNumberHolder.getCount() + 1);
                    } else {
                        return false;
                    }
                } else {
                    countMap.remove(ip);
                    lockMap.remove(ip);
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
}

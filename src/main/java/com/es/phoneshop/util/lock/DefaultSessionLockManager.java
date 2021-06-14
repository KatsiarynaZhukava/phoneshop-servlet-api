package com.es.phoneshop.util.lock;

import javax.servlet.http.HttpSession;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultSessionLockManager implements SessionLockManager {
    private static final String LOCK_SESSION_ATTRIBUTE = "sessionLock";

    @Override
    public Lock getSessionLock(HttpSession session) {
        Lock sessionLock = (Lock) session.getAttribute(LOCK_SESSION_ATTRIBUTE);
        if (sessionLock == null) {
            synchronized (session) {
                session.setAttribute(LOCK_SESSION_ATTRIBUTE, sessionLock = new ReentrantLock());
            }
        }
        return sessionLock;
    }
}

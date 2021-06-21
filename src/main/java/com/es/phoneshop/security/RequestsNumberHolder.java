package com.es.phoneshop.security;

public class RequestsNumberHolder {
    private Long count;
    private Long startTime;

    public RequestsNumberHolder() {
        count = 1L;
        startTime = System.currentTimeMillis();
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}

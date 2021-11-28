package com.example.RateLimiter;


import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;



@Component
public class SlidingWindow {
    int defaultNumberOfRequests = 3;
    int timeWindowSec = 10;
    AtomicInteger currentNumberOfRequests = new AtomicInteger(0);
    Queue<Long> timestamps = new LinkedList<>();
    Long startTimestamp = System.currentTimeMillis();
    final Object enqLock = new Object();
    final Object deqLock = new Object();

    public void decrementNumber() {
        synchronized (deqLock) {
            timestamps.removeIf((time) -> System.currentTimeMillis() - time > timeWindowSec * 1000L);
            if (timestamps.size() != currentNumberOfRequests.get()) {
                currentNumberOfRequests.set(timestamps.size());
                System.out.println("Decrement: current size " + currentNumberOfRequests + "time " + (System.currentTimeMillis() - startTimestamp));
            }
        }
    }

    public void incrementNumber() {
        synchronized (enqLock) {
            if (getCurrentNumberOfRequests() < defaultNumberOfRequests) {
                currentNumberOfRequests.incrementAndGet();
                timestamps.offer(System.currentTimeMillis());
                System.out.println("Success: current size " + currentNumberOfRequests + "time " + (System.currentTimeMillis() - startTimestamp));
            } else
                System.out.println("Refused: current size " + currentNumberOfRequests + "time " + (System.currentTimeMillis() - startTimestamp));
        }
    }

    public int getCurrentNumberOfRequests() {
        return currentNumberOfRequests.get();
    }
}

package com.example.RateLimiter;


import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SlidingWindow {
    int defaultNumberOfRequests = 3;
    int timeWindowSec = 10;
    AtomicInteger currentNumberOfRequests = new AtomicInteger(0);
    Queue<Long> timestamps = new LinkedList<>();
    Long startTimestamp = System.currentTimeMillis();
    final ReentrantLock enqLock = new ReentrantLock();
    final ReentrantLock deqLock = new ReentrantLock();

    //    @Scheduled(fixedRate = 100)
    public void decrementNumber() {
        deqLock.lock();
        try {
            timestamps.removeIf((time) -> {
                return System.currentTimeMillis() - time > timeWindowSec * 1000;
            });
            if (timestamps.size() != currentNumberOfRequests.get()) {
                currentNumberOfRequests.set(timestamps.size());
                System.out.println("Decrement: current size " + currentNumberOfRequests + "time " + (System.currentTimeMillis() - startTimestamp));
            }
        } finally {
            deqLock.unlock();
        }
    }

    //    @Scheduled(fixedRate = 2000)
    public void incrementNumber() {
        enqLock.lock();
        try {
            if (getCurrentNumberOfRequests() < defaultNumberOfRequests) {
                currentNumberOfRequests.incrementAndGet();
                timestamps.offer(System.currentTimeMillis());
                System.out.println("Success: current size " + currentNumberOfRequests + "time " + (System.currentTimeMillis() - startTimestamp));
            } else
                System.out.println("Refused: current size " + currentNumberOfRequests + "time " + (System.currentTimeMillis() - startTimestamp));
        } finally {
            enqLock.unlock();
        }
    }

    public int getCurrentNumberOfRequests() {
        return currentNumberOfRequests.get();
    }
}

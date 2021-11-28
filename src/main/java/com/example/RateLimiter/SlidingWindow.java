package com.example.RateLimiter;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SlidingWindow {
    final int defaultNumberOfRequests = 3;
    final int timeWindowSec = 10;
    final long startTimestamp = System.currentTimeMillis();
    final Queue<Long> timestamps = new LinkedList<>();
    final AtomicInteger currentNumberOfRequests = new AtomicInteger(0);
    final Object enqLock = new Object();
    final Object deqLock = new Object();

    public void decrementNumber() {
        synchronized (deqLock) {
            // delete expired requests
            timestamps.removeIf((time) -> System.currentTimeMillis() - time > timeWindowSec * 1000L);
            if (timestamps.size() != currentNumberOfRequests.get()) {
                // update the current number of requests in window
                currentNumberOfRequests.set(timestamps.size());
                System.out.println("Decrement: current size " + currentNumberOfRequests + ", time " + (System.currentTimeMillis() - startTimestamp));
            }
        }
    }

    public void incrementNumber() {
        synchronized (enqLock) {
            if (currentNumberOfRequests.get() < defaultNumberOfRequests) {
                // add new request if there is a place
                currentNumberOfRequests.incrementAndGet();
                timestamps.offer(System.currentTimeMillis());
                System.out.println("Success: current size " + currentNumberOfRequests + ", time " + (System.currentTimeMillis() - startTimestamp));
            } else {
                // no place, reject request
                System.out.println("Refused: current size " + currentNumberOfRequests + ", time " + (System.currentTimeMillis() - startTimestamp));
            }
        }
    }
}

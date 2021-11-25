package com.example.RateLimiter;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RateLimiterApplicationTests {
    @Autowired
    SlidingWindow window;

    @Scheduled(fixedRate = 100)
    public void decorateDecrementNumber(){
		window.decrementNumber();
	}

	@Scheduled(fixedRate = 2000)
    public void decorateIncrementNumber(){
		window.incrementNumber();
	}

	@Test
	@Order(2)
	public void tes15SecProcess() {
		long time = System.currentTimeMillis() - window.startTimestamp;
		while(time < 17000 || time > 18000){
			time = System.currentTimeMillis() - window.startTimestamp;
		}
		assertEquals(3,window.defaultNumberOfRequests);
		assertEquals(3,window.timestamps.size());
	}

	@Test
	@Order(1)
	public void test5SecProcess() {
		long time = System.currentTimeMillis() - window.startTimestamp;
		while(time < 5000 || time > 6000){
			time = System.currentTimeMillis() - window.startTimestamp;
		}
		assertEquals(3,window.defaultNumberOfRequests);
		assertEquals(3,window.timestamps.size());
	}





}

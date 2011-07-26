package com.wwm.util;

import java.util.concurrent.Semaphore;

import org.junit.Test;

import com.wwm.util.FastSemaphore;

public class FastSemaphoreTest {
	
	
	@Test public void testSemaphore() throws InterruptedException {
//		Semaphore s = new Semaphore(1, false);
		FastSemaphore s = new FastSemaphore(1);
		
		long start = System.currentTimeMillis();

		for (int i = 0; i < 10000000; i++) {
			s.acquire();
			s.release();
		}
		
		long duration = System.currentTimeMillis() - start;
		
		System.out.println("Semaphore time " + duration + "ms");
		
	}
}

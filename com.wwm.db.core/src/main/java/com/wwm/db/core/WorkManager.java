package com.wwm.db.core;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jsr166y.ForkJoinPool;

public class WorkManager {

	static private WorkManager instance = new WorkManager();
	
	private ExecutorService executor = Executors.newFixedThreadPool(2);
	private ForkJoinPool forkJoinPool = new ForkJoinPool(2);
	
	public static WorkManager getInstance() {
		return instance;
	}
	
	public ExecutorService getExecutorService() {
		return executor;
	}
	
	public ForkJoinPool getForkJoinExecutor() {
		return forkJoinPool;
	}

	public void invokeAllAndRethrowExceptions(List<Callable<Void>> tasks) {
		if (tasks.size() == 0) {
			return;
		}
		
		Callable<Void> firstTask = tasks.get(0);
		try {
			@SuppressWarnings("unchecked")
			List<Future<Void>> futures = tasks.size() > 1 
					? executor.invokeAll(tasks.subList(1, tasks.size()))
							: Collections.EMPTY_LIST;
		
			firstTask.call();
			for (Future<Void> future : futures) {
				future.get();
			}
		}
		catch (InterruptedException e) {
			throw new RuntimeException("Unexpected exception from threadPool", e);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			// If any call() throws an exception, then we unwrap.
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException)e.getCause();
			}
			throw new RuntimeException(e);
		}
	}
}

package com.wwm.db.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import jsr166y.ForkJoinPool;

public class WorkManager {

	static private WorkManager instance = new WorkManager();
	
	private ExecutorService executor = Executors.newFixedThreadPool(3);
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

	@SuppressWarnings("unchecked")
	public void invokeAllAndRethrowExceptions(List<Callable<Void>> tasks) {
		if (tasks.size() == 0) {
			return;
		}
		
		Callable<Void> firstTask = tasks.get(0);
		
		List<Future<Void>> futures = null;
		try {
			futures = (tasks.size() > 1 
					? submitAll(tasks.subList(1, tasks.size())) : Collections.EMPTY_LIST);
		
			// do first one in this thread (could do more than one - I want to loan this thread to the pool
			firstTask.call();
			
			// wait for rest to complete
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
		finally {
			if (futures != null) {
    			for (Future<Void> future : futures) {
    				if (!future.isDone()){
    					future.cancel(true);
    				}
    			}
			}
		}
	}

	// we're not interested in timeout
	public <T> List<Future<T>> submitAll(List<Callable<T>> tasks) {
        List<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());

        for (Callable<T> t : tasks) {
            Future<T> f = executor.submit(t);
            futures.add(f);
        }
        return futures;
	}
	
	public <T> Future<T> submit( Callable<T> task) {
		return executor.submit(task);
	}
}

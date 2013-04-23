package com.time.wzc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * author:汪至诚
 * 辅助类，为整个应用提供一个线程池
 */
public class ThreadPoolUtils {
	 private ThreadPoolUtils(){}
	 //核心线程数
	 private static int CORE_POOL_SIZE = 5;
	 //最大线程数
	 private static int MAX_POOL_SIZE = 100;
	 //额外线程生存时间
	 private static int KEEP_ALIVE_TIME = 10000;
	 //阻塞队列
	 private static BlockingQueue<Runnable> workQueue = 
			 new ArrayBlockingQueue<Runnable>(10);
	 //线程工厂
	 private static ThreadFactory threadFactory = new ThreadFactory() {
		 private final AtomicInteger integer = new AtomicInteger();
	     	public Thread newThread(Runnable r) {
	     		return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
	        }
	    };
	    
	  //线程池
	  private static ThreadPoolExecutor threadPool;
	  	static {
	        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
	                MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
	                threadFactory);
	    }
	  // 从线程池中抽取线程，执行指定的Runnable对象
	  	public static void execute(Runnable runnable){
	        threadPool.execute(runnable);
	    }
	 
}

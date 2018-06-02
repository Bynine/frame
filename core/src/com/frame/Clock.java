package com.frame;

public class Clock {
	
	private final long start_time;

	/**
	 * An object for determining elapsed time. For debugging purposes.
	 */
	public Clock(){
		start_time = System.nanoTime();
	}
	
	public void print_elapsed_time(){
		long elapsed_time = System.nanoTime() - start_time;
		System.out.println(elapsed_time + " ns");
	}
	
}
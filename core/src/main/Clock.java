package main;

/**
 * An object for determining elapsed time. For debugging purposes.
 */
public class Clock {
	
	private final long start_time;

	public Clock(){
		start_time = System.nanoTime();
	}
	
	public void print_elapsed_time(){
		long elapsed_time = System.nanoTime() - start_time;
		System.out.println(elapsed_time + " ns");
	}
	
}
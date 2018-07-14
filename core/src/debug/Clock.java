package debug;

/**
 * An object for determining elapsed time. For debugging purposes.
 */
public class Clock {
	
	private final long startTime;

	public Clock(){
		startTime = System.nanoTime();
	}
	
	public void printElapsedTime(){
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println(elapsedTime + " ns");
	}
	
}
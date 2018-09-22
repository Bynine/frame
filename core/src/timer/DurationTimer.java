package timer;

/**
 * A timer that starts from 0 and counts up.
 */
public class DurationTimer extends Timer {

	public DurationTimer(int endTime) {
		super(endTime);
		counter = 0;
	}
	
	public DurationTimer(int endTime, int interval){
		this(endTime);
		this.interval = interval;
	}

}

package timer;

/**
 * A timer that starts from 0 and counts up.
 */
public class TimerDuration extends Timer {

	public TimerDuration(int endTime) {
		super(endTime);
		counter = 0;
	}
	
	public TimerDuration(int endTime, int interval){
		this(endTime);
		this.interval = interval;
	}

}

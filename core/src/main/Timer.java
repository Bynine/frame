package main;

public class Timer {

	protected int endTime;
	protected int interval;
	protected float counter;

	public Timer(int endTime){
		this.endTime = endTime;
		counter = endTime + 1;
	}
	
	public Timer(int endTime, int interval){
		this(endTime);
		this.interval = interval;
	}

	public void reset(){ 
		counter = 0;
	}

	public void reset(int i){ 
		counter = i;
	}

	public void countUp(){ 
		counter += FrameEngine.elapsedTime; 
	}

	public void countDown(){ 
		counter -= FrameEngine.elapsedTime;  
	}

	public void setEndTime(int endTime){ 
		this.endTime = endTime; 
	}

	public boolean timeUp(){ 
		return getCounter() > endTime; 
	}
	
	public boolean pastTime(int time){
		return getCounter() > time;
	}

	public int getCounter(){ 
		return (int)counter; 
	}

	public int getEndTime(){ 
		return endTime; 
	}

	public void end(){ 
		counter = endTime + 2; 
	}

	public void change(int i) {
		counter += i;
	}

}
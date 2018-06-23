package main;

public class Timer {

	protected int endTime;
	protected float counter;

	public Timer(int endTime){
		this.endTime = endTime;
		counter = endTime + 1;
	}

	public void reset(){ 
		counter = 0;
	}

	public void reset(int i){ 
		setEndTime(i);
		counter = 0; 
	}

	public void countUp(){ 
		counter += FrameEngine.elapsed_time; 
	}

	public void countDown(){ 
		counter -= FrameEngine.elapsed_time;  
	}

	public void setEndTime(int endTime){ 
		this.endTime = endTime; 
	}

	public boolean timeUp(){ 
		return (counter > endTime); 
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

}
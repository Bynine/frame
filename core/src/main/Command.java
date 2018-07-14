package main;

public class Command {
	
	private final String ID;

	public Command(String ID){
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}
	
	@Override
	public String toString(){
		return ID;
	}
}

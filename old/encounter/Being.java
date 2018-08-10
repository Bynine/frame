package encounter;

import java.util.ArrayList;
import main.TSVReader;

/**
 * Represents a character in an Encounter.
 */
public class Being {
	
	public static final String DEFAULT_STATE = "default";
	
	private final String name;
	private final int MAX_HEALTH;
	private final ArrayList<Reaction> reactions = new ArrayList<Reaction>();
	
	private int health;
	private String state = DEFAULT_STATE;

	Being(String ID){
		String[] data = new TSVReader().loadDataByID(ID, TSVReader.ENCOUNTERS_URL);
		name = data[1];
		MAX_HEALTH = Integer.parseInt(data[2]);
		health = MAX_HEALTH;
		String[] allReactionData = data[3].split(",");
		for (String reactionData: allReactionData){
			Cause cause;
			Action action;
			if (reactionData.equals(allReactionData[0])){ // default, no cause
				cause = new Cause();
				action = new Action(reactionData);
			}
			else{
				String[] causeAndAction = reactionData.split(":");
				cause = new Cause(causeAndAction[0]);
				action = new Action(causeAndAction[1]);
			}
			reactions.add(new Reaction(cause, action));
		}
	}
	
	Action chooseReaction(Action action){
		for (Reaction reaction: reactions){
			if (reaction.causedBy(action)){
				return reaction.getAction();
			}
		}
		System.out.println("Chose default action");
		return reactions.get(0).getAction(); // default
	}
	
	void takeDamage(int damage){
		health -= damage;
	}
	
	boolean isFainted(){
		return health <= 0;
	}
	
	String getState(){
		return state;
	}
	
	public String getName(){
		return name;
	}
	
}

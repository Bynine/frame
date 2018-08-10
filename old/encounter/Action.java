package encounter;

import java.util.Collection;
import java.util.HashSet;

import main.FrameEngine;
import main.TSVReader;

/**
 * Performed by Beings during Encounters.
 */
public class Action {
	
	private final HashSet<Trait> traits = new HashSet<>();
	private final String performString;
	private final String ID;
	
	public Action(String ID){
		this.ID = ID;
		String data[] = new TSVReader().loadDataByID(ID, TSVReader.ACTION_URL);
		performString = data[1];
		for (String trait: data[2].split(",")){
			traits.add(new Trait(trait));
		}
	}

	public Action(String ID, String performString, Collection<Trait> traits){
		this.ID = ID;
		this.performString = performString;
		this.traits.addAll(traits);
	}
	
	public void perform(){
		FrameEngine.getEncounter().putTextbox(performString);
	}
	
	public Collection<Trait> getTraits(){
		return traits;
	}
	
	/**
	 * Any Action with the same ID is equivalent.
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Action)) return false;
		return this.ID.equals(((Action)o).ID);
	}
	
	@Override
	public String toString(){
		return ID;
	}
	
	public static boolean isAction(String action){
		return action.startsWith("A_");
	}

}

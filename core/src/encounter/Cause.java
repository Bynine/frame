package encounter;

import java.util.HashSet;

/**
 * All potential 
 */
public class Cause {

	private final HashSet<Trait> traits = new HashSet<Trait>();
	private final HashSet<Action> actions = new HashSet<Action>();
	
	public Cause(String... causes){
		for(String cause: causes){
			if (Action.isAction(cause)) actions.add(new Action(cause));
			if (Trait.isTrait(cause)) traits.add(new Trait(cause));
		}
	}
	
	public boolean doesCause(Action action){
		for (Action actionThis: actions){
			if (action.equals(actionThis)) return true;
		}
		for (Trait trait: action.getTraits()){
			for (Trait traitThis: traits){
				if (trait.equals(traitThis)) return true;
			}
		}
		return false;
	}
	
}

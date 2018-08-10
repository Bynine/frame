package encounter;

/**
 * A component of an Action that describes an attribute of it.
 */
public class Trait{

	private final String ID;
	
	Trait(String ID){
		this.ID = ID;
	}
	
	/**
	 * Any trait with the same ID is equivalent.
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Trait)) return false;
		return this.ID.equals(((Trait)o).ID);
	}
	
	public static boolean isTrait(String action){
		return action.startsWith("T_");
	}
	
}

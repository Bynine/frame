package encounter;

/**
 * Contains an Action and the Cause for that Action to occur.
 */
public class Reaction {

	private final Cause cause;
	private final Action action;

	Reaction(Cause cause, Action action){
		this.cause = cause;
		this.action = action;
	}

	public Action getAction(){
		return action;
	}

	public boolean causedBy(Action action){
		return cause.doesCause(action);
	}

}

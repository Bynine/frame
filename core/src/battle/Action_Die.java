package battle;

public class Action_Die extends Action {

	public Action_Die(Tech tech){
		if (!tech.user.alive()) return;
		tech.user.take_damage(Integer.MAX_VALUE);
		System.out.println(tech.user.nickname + " exploded!!");
	}
}

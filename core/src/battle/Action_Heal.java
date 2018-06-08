package battle;

public class Action_Heal extends Action {
	
	public Action_Heal(Tech tech, Monster target){
		int heal = (int)
				((tech.pow * tech.user.curr_stats[Monster.EMP])
				/ 10.0 
				* ((tech.user.level + 19.0) / 20.0))
				;
		target.heal(heal);
		System.out.println(tech.user.nickname + " restores " + heal + " health of " + target.nickname);
	}
}

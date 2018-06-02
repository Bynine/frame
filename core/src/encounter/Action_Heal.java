package encounter;

public class Action_Heal extends Action {
	
	public Action_Heal(Tech tech, Monster target){
		int heal = (int)
				((tech.pow * tech.user.curr_stats[Monster.EMP])
				/ 10.0 
				* ((tech.user.level + 19.0) / 20.0))
				;
		target.curr_stats[Monster.VIT] += heal;
		System.out.println(tech.user.nickname + " does " + heal + " healing to " + target.nickname);
	}
}

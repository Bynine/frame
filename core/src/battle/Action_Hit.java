package battle;

public class Action_Hit extends Action {

	public Action_Hit(Tech tech, Monster target){
		int damage = (int)
				((tech.pow * tech.user.curr_stats[Monster.POW])
				/ (2.0 + (target.curr_stats[Monster.DEF] * 1.6))
				* ((tech.user.level + 19.0) / 25.0)) + 1
				;
		target.take_damage(damage);
		System.out.println(tech.user.nickname + " does " + damage + " damage to " + target.nickname);
	}
}

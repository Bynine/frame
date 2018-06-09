package battle;

public class Action_LowerStat extends Action {

	public Action_LowerStat(Tech tech, Monster target, int stat_pos){
		int change = (int) -(target.getRealStats()[stat_pos] * 0.35);
		target.change_stat(change, stat_pos);
		System.out.println(tech.user.nickname + " drains " + target.nickname + 
				" with " + tech.name + " by " + change);
	}
	
}

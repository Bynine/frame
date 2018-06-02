package encounter;

public class Action_Explode extends Action {

	public Action_Explode(Tech tech){
		tech.user.curr_stats[Monster.VIT] = 0;
		System.out.println(tech.user.nickname + " exploded!!");
	}
}

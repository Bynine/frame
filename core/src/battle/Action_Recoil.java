package battle;

public class Action_Recoil extends Action {

	public Action_Recoil(Tech tech){
		int damage = (int)(0.05 * tech.pow * tech.user.getRealStats()[Monster.VIT]);
		tech.user.take_damage(damage); 
		System.out.println(tech.user.nickname + " takes " + damage + " from its reckless attack!");
	}
	
}

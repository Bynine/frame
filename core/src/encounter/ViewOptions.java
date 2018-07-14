package encounter;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import main.Button;

public class ViewOptions extends View {

	private final ArrayList<Button> CHOICES = makeButtons(
			new Vector2(4, 2),
			new ArrayMap<String, Encounter.Choice>(){{
				put("Skills", Encounter.Choice.SKILL);
				put("Inventory", Encounter.Choice.ITEM);
				put("Leave", Encounter.Choice.END);
			}}
			);

	@Override
	public ArrayList<Button> getButtons() {
		return CHOICES;
	}

}

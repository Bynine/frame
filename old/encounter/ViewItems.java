package encounter;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import main.Button;

public class ViewItems extends View {

	private final ArrayList<Button> CHOICES = makeButtons(
			new Vector2(4, 2),
			new ArrayMap<String, Action>(){{
				put("Apple", new Action("A_FEED"));
			}}
			);

	@Override
	public ArrayList<Button> getButtons() {
		return CHOICES;
	}

}

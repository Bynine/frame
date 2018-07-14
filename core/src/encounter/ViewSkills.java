package encounter;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import main.Button;

public class ViewSkills extends View {

	private final ArrayList<Button> CHOICES = makeButtons(
			new Vector2(4, 2),
			new ArrayMap<String, Action>(){{
				put("Wait", new Action("A_WAIT"));
				put("Meh", new Action("A_NOTHANKS"));
			}}
			);

	@Override
	public ArrayList<Button> getButtons() {
		return CHOICES;
	}

}

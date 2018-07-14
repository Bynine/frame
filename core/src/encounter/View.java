package encounter;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import main.Button;
import main.FrameEngine;
import main.GraphicsHandler;

public abstract class View {

	public abstract ArrayList<Button> getButtons();

	protected ArrayList<Button> makeButtons(
			Vector2 size, ArrayMap<String, ? extends Object> map
			){
		ArrayList<Button> buttons = new ArrayList<>();
		for (int ii = 0; ii < map.size; ++ii){
			Rectangle rect = new Rectangle(
					((Gdx.graphics.getWidth() - (2 * size.x * FrameEngine.TILE))) / (2/GraphicsHandler.ZOOM),
					FrameEngine.TILE * 2 + ( (size.y * (2.0f/3.0f) + 1) * FrameEngine.TILE * (map.size - ii - 1)), 
					size.x * FrameEngine.TILE,
					size.y * FrameEngine.TILE
					);
			String label = map.getKeyAt(ii);
			buttons.add(new Button(
					new Rectangle(rect), 
					label, 
					map.get(label)
					));
		}
		return buttons;
	}

}

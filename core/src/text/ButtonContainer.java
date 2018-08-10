package text;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import main.FrameEngine;
import main.GraphicsHandler;

/**
 * Creator and container for button options.
 */
public class ButtonContainer {

	private final ArrayList<Button> buttons = new ArrayList<>();
	private int position = 0;

	public ButtonContainer(Vector2 size, ArrayMap<String, String> map){
		for (int ii = 0; ii < map.size; ++ii){
			float x = 
					((Gdx.graphics.getWidth() - (2 * size.x * FrameEngine.TILE))) / 
					(2/GraphicsHandler.ZOOM);
			float y = 
					FrameEngine.TILE * 4 + ( (size.y * (2.0f/3.0f) + 1) * 
							FrameEngine.TILE * (map.size - ii - 1));
			Rectangle rect = new Rectangle(
					x,
					y, 
					size.x * FrameEngine.TILE,
					size.y * FrameEngine.TILE
					);
			String label = map.getKeyAt(ii);
			String output = map.get(label);
			if (label.equals("YES")) label = "Nod head";
			if (label.equals("NO")) label = "Shake head";
			buttons.add(new Button(
					new Rectangle(rect), 
					label, 
					output
					));
		}
	}

	public void update(){
		if (FrameEngine.getInputHandler().getDownJustPressed()) move(1);
		if (FrameEngine.getInputHandler().getUpJustPressed()) move(-1);
	}

	/**
	 * Move the cursor to the selected button.
	 */
	private void move(int i){
		position = MathUtils.clamp(position + i, 0, buttons.size()-1);
	}

	public String getChoice(){
		return (String) (buttons.get(position).getOutput());
	}

	public ArrayList<Button> getButtons(){
		return buttons;
	}

	public int getPosition() {
		return position;
	}

}

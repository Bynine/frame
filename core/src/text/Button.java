package text;

import com.badlogic.gdx.math.Vector2;

/**
 * Holds a name to be displayed, an area region, and an output to be evaluated.
 */
// TODO: Rename to MenuOption
public class Button {
	
	private final Vector2 dim;
	private final String name;
	private final Object output;

	public Button(int width, int height, String name, Object output) {
		this.dim = new Vector2(width, height);
		this.name = name;
		this.output = output;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getOutput(){
		return output;
	}
	
	public Vector2 getDimensions(){
		return dim;
	}
	
	public boolean clicked(){
		return false;
	}

}

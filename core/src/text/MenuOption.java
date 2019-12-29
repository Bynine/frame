package text;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

/**
 * Holds a name to be displayed, an area region, and an output to be evaluated.
 */
public class MenuOption {
	
	private final Vector2 dim;
	private final String name;
	private final Object output;
	private final Map<String, String> properties = new HashMap<String, String>();

	public MenuOption(int width, int height, String name, Object output) {
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
	
	public Map<String, String> getProperties(){
		return properties;
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

}

package text;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import main.ItemDescription;

/**
 * Holds a name to be displayed, an area region, and an output to be evaluated.
 */
public class MenuOption implements Comparable<MenuOption>{
	
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

	@Override
	public int compareTo(MenuOption mo2) {
		if (null != output && null != mo2.output && output instanceof ItemDescription && mo2.output instanceof ItemDescription) {
			int comparison = 0;
			ItemDescription thisDesc = (ItemDescription)output;
			ItemDescription thatDesc = (ItemDescription)mo2.output;
			if (thisDesc.hasAttribute("ACTION")) {
				comparison -= 2;
			}
			if (thatDesc.hasAttribute("ACTION")){
				comparison += 2;
			}
			if (thisDesc.hasAttribute("TREASURE")) {
				comparison -= 1;
			}
			if (thatDesc.hasAttribute("TREASURE")) {
				comparison += 1;
			}
			return comparison;
		}
		return 0;
	}

}

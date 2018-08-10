package text;

import com.badlogic.gdx.math.Rectangle;

/**
 * Holds a name to be displayed, a selectable region, and an output to be evaluated.
 */
public class Button {
	
	private final Rectangle area;
	private final String name;
	private final Object output;

	public Button(Rectangle area, String name, Object output) {
		this.area = area;
		this.name = name;
		this.output = output;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getOutput(){
		return output;
	}
	
	public Rectangle getArea(){
		return area;
	}
	
	public boolean clicked(){
		return false;
	}

}

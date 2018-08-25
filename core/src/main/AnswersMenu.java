package main;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.utils.ArrayMap;

import text.Button;

public class AnswersMenu extends AbstractMenu{
	
	private final ArrayList<Button> options = new ArrayList<Button>();
	
	AnswersMenu(ArrayMap<String, String> map){
		for (String key: map.keys()){
			options.add(
					new Button(
							6, 2,
							map.get(key), 
							key)
					);
		}
	}

	@Override
	public List<Button> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		// TODO
	}

}

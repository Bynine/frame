package main;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.utils.ArrayMap;

import text.MenuOption;

public class AnswersMenu extends AbstractMenu{
	
	private final ArrayList<MenuOption> options = new ArrayList<MenuOption>();
	
	AnswersMenu(ArrayMap<String, String> map){
		for (String key: map.keys()){
			options.add(
					new MenuOption(
							6, 2,
							map.get(key), 
							key)
					);
		}
	}

	@Override
	public List<MenuOption> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		AudioHandler.playSound(select);
	}

}

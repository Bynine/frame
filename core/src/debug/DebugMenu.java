package debug;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import main.TSVReader;
import text.MenuOption;
import main.FrameEngine;
import main.AbstractMenu;

/**
 * Used to access various content for debugging purposes.
 */
public class DebugMenu extends AbstractMenu{

	private final ArrayList<MenuOption> mapIDs = new ArrayList<>();

	/**
	 * Loads all map names but the headers.
	 */
	public DebugMenu(){
		perColumn = 10;
		String[] mapData = new TSVReader().loadAllData(TSVReader.MAP_URL);
		for (String data: mapData){	
			if (!data.split(TSVReader.split)[0].matches("\\s")){
				final String id = data.split(TSVReader.split)[0];
				String name = id;
				if (name.startsWith("FROST_")) {
					name = "F_" + name.substring(6);
				}
				mapIDs.add(new MenuOption(
						4, 1,
						name.substring(0, Math.min(9, name.length())),
						id
						));
			}
		}
	}

	@Override
	public List<MenuOption> getList(){
		return mapIDs;
	}

	@Override
	protected void moveCursorHorizontal(int i){
		super.moveCursorHorizontal(i);
		cursor = MathUtils.clamp(cursor + (i*perColumn), 0, getList().size() - 1);
	}

	@Override
	protected void selectItem() {
		FrameEngine.debugAreaChange(getActiveButton().getOutput().toString());
	}

	public Vector2 getButtonPosition(int pos) {
		Vector2 superPosition = super.getButtonPosition(pos);
		superPosition.add(-FrameEngine.TILE*7, FrameEngine.TILE*1);
		return superPosition;
	}

}

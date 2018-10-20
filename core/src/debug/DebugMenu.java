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
				mapIDs.add(new MenuOption(
						4, 1,
						data.split(TSVReader.split)[0],
						data.split(TSVReader.split)[0]
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
		superPosition.add(-FrameEngine.TILE*7, FrameEngine.TILE*2);
		return superPosition;
	}

}

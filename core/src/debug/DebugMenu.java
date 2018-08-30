package debug;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import main.TSVReader;
import text.Button;
import main.FrameEngine;
import main.AbstractMenu;

/**
 * Used to access various content for debugging purposes.
 */
public class DebugMenu extends AbstractMenu{

	private final ArrayList<Button> mapIDs = new ArrayList<>();
	public static final int horizontalMove = 5;
	private static final int HEADER_ROWS = 1;

	/**
	 * Loads all map names but the headers.
	 */
	public DebugMenu(){
		String[] mapData = new TSVReader().loadAllData(TSVReader.MAP_URL);
		int ii = 0;
		for (String data: mapData){	
			if (ii >= HEADER_ROWS) mapIDs.add(new Button(
					5, 2,
					data.split(TSVReader.split)[0],
					data.split(TSVReader.split)[0]
							));
			ii++;
		}
	}

	@Override
	public List<Button> getList(){
		return mapIDs;
	}
	
	@Override
	protected void moveCursorHorizontal(int i){
		super.moveCursorHorizontal(i);
		cursor = MathUtils.clamp(cursor + (i*horizontalMove), 0, getList().size() - 1);
	}

	@Override
	protected void selectItem() {
		FrameEngine.initiateAreaChange(getActiveButton().getOutput().toString());
	}

}

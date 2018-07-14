package debug;

import java.util.ArrayList;

import main.TSVReader;
import main.FrameEngine;
import main.Timer;

/**
 * Used to access various content for debugging purposes.
 */
public class DebugMenu {

	private final ArrayList<String> mapIDs = new ArrayList<>();
	private static final int HEADER_ROWS = 2;
	private int cursor = 0;
	private final Timer cursorHalt = new Timer(5);

	/**
	 * Loads all map names but the headers.
	 */
	public DebugMenu(){
		String[] mapData = new TSVReader().loadAllData(TSVReader.MAP_URL);
		int ii = 0;
		for (String data: mapData){	
			if (ii >= HEADER_ROWS) mapIDs.add(data.split(TSVReader.split)[0]);
			ii++;
		}
	}
	
	/**
	 * Called every frame while in Debug state.
	 */
	public void update(){
		acceptInputs();
		cursorHalt.countUp();
	}

	/**
	 * Updates based on input handler.
	 */
	private void acceptInputs(){
		if (FrameEngine.getInputHandler().getActionJustPressed()){
			FrameEngine.initiateAreaChange(getSelectedMapID());
		}
		if (cursorHalt.timeUp()){
			moveCursor((int)FrameEngine.getInputHandler().getYInput());
			cursorHalt.reset();
		}
	}
	
	private void moveCursor(int i){
		cursor += i;
		if (cursor < 0) cursor = 0;
		else if (cursor >= mapIDs.size()) cursor = mapIDs.size() - 1;
	}

	public ArrayList<String> getMapIDs(){
		return mapIDs;
	}

	public String getSelectedMapID(){
		return mapIDs.get(cursor);
	}

}

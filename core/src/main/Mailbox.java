package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

public class Mailbox extends AbstractMenu {
	
	protected final ArrayList<MenuOption> descs = new ArrayList<MenuOption>();
	public static final String CLOSE = "CLOSE", OPENED = "OPENED";
	private TSVReader letterReader = new TSVReader();
	
	Mailbox(){
		perColumn = 5; 
		String[] letters = letterReader.loadAllData(TSVReader.LETTER_URL);
		SaveFile save = FrameEngine.getSaveFile();
		for (String letterString: letters) {
			String[] letterData = letterString.split("\t");
			String[] saveData = letterData[1].split(TSVReader.short_split);
			boolean hasLetter = true;
			for (String flag: saveData) {
				if (!save.getFlag(flag)) hasLetter = false;
			}
			if (FrameEngine.LETTERS) hasLetter = true;
			if (hasLetter) {
				final String id = letterData[0];
				addLetter(id);
				String read = id + "_LETTER_READ";
				if (!save.getFlag(read)) {
					FrameEngine.mailToRead = true;
				}
				save.setFlag(read, true);
			}
		}
		updateRead();
	}
	
	private void addLetter(String id) {
		descs.add(new MenuOption(2, 2, "", new ArrayList<TextureRegion>(Arrays.asList(
				new TextureRegion(new Texture(Gdx.files.internal("letters/" + id.toLowerCase() + ".png")))
				))));
	}
	
	protected void moveCursorHorizontal(int i){
		int n = i * perColumn;
		playCursorSound(n);
		cursor = MathUtils.clamp(cursor + (n), 0, getList().size() - 1);
		updateRead();
	}

	protected void moveCursorVertical(int i) {
		super.moveCursorVertical(i);
		updateRead();
	}
	
	private void updateRead() {
		if (this.getActiveButton() != null) {
			this.getActiveButton().setProperty(OPENED, "true");
		}
	}

	@Override
	protected void selectItem() {
		//
	}
	
	public Vector2 getButtonPosition(int pos) {
		Vector2 position = super.getButtonPosition(pos);
		position.x -= FrameEngine.TILE * 6.5f;
		position.y += FrameEngine.TILE * 1;
		return position;
	}
	
	@Override
	public List<MenuOption> getList() {
		return descs;
	}

}

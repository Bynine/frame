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
		int ii = 0;
		for (String letterString: letters) {
			++ii;
			String[] letterData = letterString.split("\t");
			String[] saveData = letterData[1].split(TSVReader.short_split);
			boolean hasLetter = true;
			for (String flag: saveData) {
				if (!save.getFlag(flag)) hasLetter = false;
			}
			if (FrameEngine.LETTERS && ii < 3) hasLetter = true;
			if (hasLetter) {
				final String id = letterData[0];
				String read = id + "_LETTER_READ";
				boolean isRead = save.getFlag(read);
				if (!isRead) {
					FrameEngine.mailToRead = true;
				}
				addLetter(id, isRead);
				save.setFlag(read, true);
			}
		}
		updateRead(false);
	}
	
	private void addLetter(String id, boolean isRead) {
		MenuOption mo = new MenuOption(2, 2, "", new ArrayList<TextureRegion>(Arrays.asList(
				new TextureRegion(new Texture(Gdx.files.internal("letters/" + id.toLowerCase() + ".png"))),
				new TextureRegion(new Texture(Gdx.files.internal("sprites/gui/letter_" + id.toLowerCase() + ".png"))),
				new TextureRegion(new Texture(Gdx.files.internal("sprites/gui/letter_" + id.toLowerCase() + "_open.png")))
				)));
		mo.setProperty(OPENED, Boolean.toString(isRead));
		System.out.println(mo.getProperties());
		descs.add(mo);
	}
	
	protected void moveCursorHorizontal(int i){
		int n = i * perColumn;
		playCursorSound(n);
		cursor = MathUtils.clamp(cursor + (n), 0, getList().size() - 1);
		updateRead(true);
	}

	protected void moveCursorVertical(int i) {
		super.moveCursorVertical(i);
		updateRead(true);
	}
	
	private void updateRead(boolean move) {
		if (this.getActiveButton() != null) {
			if (move && !this.getActiveButton().getProperties().get(OPENED).equals("true")) {
				AudioHandler.playPitchedSound(openMap, 1.1f, 0.5f, false);
			}
			this.getActiveButton().setProperty(OPENED, "true");
			FrameEngine.mailToRead = false;
			for (MenuOption mo: this.getList()) {
				if (!mo.getProperties().get(OPENED).equals("true")) {
					FrameEngine.mailToRead = true;
				}
			}
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

package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

/**
 * Represents player's items.
 */
public class Inventory extends AbstractMenu{

	protected final ArrayList<String> items = new ArrayList<String>();
	protected final ArrayList<MenuOption> descs = new ArrayList<MenuOption>();

	Inventory(){
		if (FrameEngine.FULLINV){
			items.add("MAP");
			items.add("KEY");
			items.add("SHOVEL");
			items.add("TREASURE1");
			items.add("TREASURE2");
			items.add("TREASURE3");
			items.add("TREASURE4");
			items.add("SHELL2");
			items.add("SEED1");
			items.add("SEED2");
			items.add("SEED3");
			items.add("SEED4");
			items.add("WATERINGCAN");
			items.add("GRUB1");
			items.add("GRUB2");
			items.add("GRUB3");
		}
	}

	public boolean hasItem(String item){
		return items.contains(item);
	}

	public void addItem(String item){
		items.add(0, item);
	}

	public void addItemConditional(String item){
		if (!items.contains(item)){
			addItem(item);
		}
	}

	public void removeItem(String item){
		items.remove(item);
		Iterator<MenuOption> descIter = descs.iterator();
		while(descIter.hasNext()){
			MenuOption desc= descIter.next();
			if (((ItemDescription)desc.getOutput()).id.equals(item)){
				((ItemDescription)desc.getOutput()).dispose();
				descIter.remove();
			}
		}
		descs.remove(item);
	}

	/**
	 * descs will have a description for each item in items.
	 */
	@Override
	public void open(){
		super.open();
		updateDescriptions();
	}

	protected void updateDescriptions(){
		descs.clear();
		for (String item: items){
			descs.add(new MenuOption(2, 2, item, new ItemDescription(item)));
		}
	}

	@Override
	protected void moveCursorVertical(int i){
		int newPosition = cursor - (i * 4);
		if ((newPosition >= 0) && (newPosition < getList().size())){
			cursor = newPosition;
			AudioHandler.playSoundVariedPitch(moveCursor);
		}
		else{
			AudioHandler.playSoundVariedPitch(stopCursor);
		}
	}

	@Override
	protected void moveCursorHorizontal(int i){
		playCursorSound(i);
		cursor = MathUtils.clamp(cursor + i, 0, getList().size()-1);
	}

	@Override
	public List<MenuOption> getList() {
		return descs;
	}

	@Override
	protected void selectItem() {
		ItemDescription desc = (ItemDescription)(getActiveButton().getOutput());
		if (FrameEngine.canSelectMenuItem()) {
			if (desc.id.equals("SNAIL")){
				FrameEngine.snailActive = !FrameEngine.snailActive;
				AudioHandler.playSoundVariedPitch(FrameEngine.snailActive ? moveCursor : stopCursor);
			}
			else if (desc.id.equals("MAP")) {
				FrameEngine.showMap();
				AudioHandler.playSoundVariedPitch(openMap);
			}
		}
	}

	public Vector2 getButtonPosition(int pos) {
		int onScreen = 4;
		int posX = (pos % onScreen);
		int posY = 2 + (int) pos/onScreen;
		float x = (((posX + 0.5f) * FrameEngine.TILE) * 2);
		float y = (2 * GraphicsHandler.ZOOM) * Gdx.graphics.getHeight()/2 -
				FrameEngine.TILE - (posY * FrameEngine.TILE * 2);
		return new Vector2(x, y);
	}

	public void wipe() {
		items.clear();
		descs.clear();
	}

	public List<String> getItems() {
		return items;
	}

}

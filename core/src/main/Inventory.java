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
	private int page = 0;
	public static final int WIDTH = 4, HEIGHT = 4;

	Inventory(){
		if (FrameEngine.ALLITEMS){
			TSVReader reader = new TSVReader();
			String[] items = reader.loadAllData(TSVReader.ITEM_URL);
			for(String itemInfo: items) {
				String[] itemInfoSplit = itemInfo.split("\t");
				String itemId = itemInfoSplit[0];
				if (!itemId.trim().isEmpty()) {
					addItem(itemId);
				}
			}
		}
		else if(FrameEngine.INV) {
			addItem("COCOAHOT");
			addItem("SHOVEL");
			addItem("WATERINGCAN");
			addItem("HAMMER");
			addItem("COFFEE");
			addItem("KEY1");
			addItem("KEY2");
			addItem("BOOK");
			addItem("FLAME");
			addItem("SHELLPHONE");
		}
	}

	public boolean hasItem(String item){
		return items.contains(item);
	}

	public void addItem(String item){
		items.add(0, item);
	}

	public boolean addItemConditional(String item){
		if (!items.contains(item)){
			addItem(item);
			return true;
		}
		return false;
	}

	public boolean removeItem(String item){
		boolean removed = items.remove(item);
		Iterator<MenuOption> descIter = descs.iterator();
		while(descIter.hasNext()){
			MenuOption desc= descIter.next();
			if (((ItemDescription)desc.getOutput()).id.equals(item)){
				((ItemDescription)desc.getOutput()).dispose();
				descIter.remove();
				
			}
		}
		//descs.remove(item);
		return removed;
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
		descs.sort(null);
	}

	@Override
	protected void moveCursorVertical(int i){
		if ( (cursor < WIDTH && i > 0) || (cursor >= getList().size()-WIDTH && i < 0) ) {
			
		}
		else {
			playCursorSound(i);
			cursor = MathUtils.clamp(cursor - (i * WIDTH), 0, getList().size()-1);
			setPage();
		}
	}

	@Override
	protected void moveCursorHorizontal(int i){
		playCursorSound(i);
		cursor = MathUtils.clamp(cursor + i, 0, getList().size()-1);
		setPage();
	}

	@Override
	public List<MenuOption> getList() {
		return descs;
	}
	
	private void setPage() {
		int pagebeg = page * WIDTH;
		Vector2 range = new Vector2(
				pagebeg + WIDTH, 
				pagebeg + (WIDTH * HEIGHT) - WIDTH
				);
		if (cursor < range.x && page > 0) {
			page -= 1;
		}
		else if (cursor >= range.y+4) {
			page += 1;
		}
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
			else if (desc.id.equals("COFFEE")) {
				FrameEngine.coffeeBoost();
			}
			else if (desc.id.equals("SHELLPHONE")) {
				FrameEngine.callShellPhone();
			}
		}
	}

	public Vector2 getButtonPosition(int pos) {
		int onScreen = WIDTH;
		int posX = (pos % onScreen);
		int posY = 2 + (int) pos/onScreen;
		float x = (((0.5f + posX) * FrameEngine.TILE) * 2);
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
	
	@Override
	public int getPage() {
		return page;
	}

}

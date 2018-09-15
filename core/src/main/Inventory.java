package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import text.Button;

/**
 * Represents player's items.
 */
public class Inventory extends AbstractMenu{

	protected final ArrayList<String> items = new ArrayList<String>();
	protected final ArrayList<Button> descs = new ArrayList<Button>();

	Inventory(){
//		for (int ii = 0; ii < 30; ++ii){
//			items.add("KEEPSAKE");
//		}
	}

	public boolean hasItem(String item){
		return items.contains(item);
	}

	public void addItem(String item){
		items.add(0, item);
	}

	public void removeItem(String item){
		items.remove(item);
		Iterator<Button> descIter = descs.iterator();
		while(descIter.hasNext()){
			Button desc= descIter.next();
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
			descs.add(new Button(2, 2, item, new ItemDescription(item)));
		}
	}
	
	@Override
	protected void moveCursorVertical(int i){
		playCursorSound();
		int newPosition = cursor - (i * 5);
		if ((newPosition >= 0) && (newPosition < getList().size())){
			cursor = newPosition;
		}
	}

	@Override
	protected void moveCursorHorizontal(int i){
		playCursorSound();
		cursor = MathUtils.clamp(cursor + i, 0, getList().size()-1);
	}

	@Override
	public List<Button> getList() {
		return descs;
	}

	@Override
	protected void selectItem() {
		//
	}

	public Vector2 getButtonPosition(int pos) {
		int onScreen = 5;
		int posX = (pos % onScreen);
		int posY = 2 + (int) pos/onScreen;
		float x = ((posX * FrameEngine.TILE) * 2);
		float y = Gdx.graphics.getHeight()/2 - FrameEngine.TILE - (posY * FrameEngine.TILE * 2);
		return new Vector2(x, y);
	}

}

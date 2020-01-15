package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import main.AudioHandler;
import main.FrameEngine;

public class Dungeon extends ImmobileEntity {
	
	private boolean isHorizontal = FrameEngine.getSaveFile().getFlag("ROTATED");
	private final TextureRegion 
	horizontal = new TextureRegion(new Texture("sprites/objects/dungeon_horizontal.png")),
	vertical = new TextureRegion(new Texture("sprites/objects/dungeon_vertical.png"));
	private final Sound rotate = Gdx.audio.newSound(Gdx.files.internal("sfx/rotate.wav"));

	public Dungeon(float x, float y) {
		super(x, y);
		image = new Sprite(isHorizontal ? horizontal : vertical);
		layer = Layer.BACK;
		collides = true;
	}
	
	public void rotate() {
		isHorizontal = !isHorizontal;
		image = new Sprite(isHorizontal ? horizontal : vertical);
		FrameEngine.getSaveFile().setFlag("ROTATED", isHorizontal);
		FrameEngine.getArea().refreshCollision();
		AudioHandler.playSoundVariedPitch(rotate);
	}
	
	@Override
	public List<Rectangle> getHitboxes() {
		if (FrameEngine.DUNGEON) return new ArrayList<Rectangle>(); // no collision
		final int aaa = 5 * FrameEngine.TILE, bbb = 14 * FrameEngine.TILE;
		if (isHorizontal) {
			return new ArrayList<Rectangle>(Arrays.asList(
					new Rectangle(position.x, position.y, bbb, aaa),
					new Rectangle(position.x, position.y + (bbb - aaa), bbb, aaa)
					));
		}
		else {
			return new ArrayList<Rectangle>(Arrays.asList(
					new Rectangle(position.x, position.y, aaa, bbb),
					new Rectangle(position.x + (bbb - aaa), position.y, aaa, bbb)
					));
		}
	}

	@Override
	public void dispose() {
		horizontal.getTexture().dispose();
		vertical.getTexture().dispose();
		rotate.dispose();
	}

}

package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import encounter.Encounter;
import encounter.View;

public class EncounterGraphicsHandler extends GraphicsHandler {
	
	private TextureRegion playerImage = new TextureRegion(new Texture(
			Gdx.files.internal("sprites/encounter/player.png")
			));
	private TextureRegion opponentImage = new TextureRegion(new Texture(
			Gdx.files.internal("sprites/encounter/opponent.png")
			));
	private TextureRegion background = new TextureRegion();
	final int closeness = FrameEngine.TILE * 1;
	
	EncounterGraphicsHandler(){
		wipeColor = new Color(0.85f, 0.87f, 0.92f, 1);
		batch = new SpriteBatch();
		begin();
	}

	public void begin() {
		background = new TextureRegion(new Camera().takeSnapshot());
	}

	void draw(Encounter encounter){
		View view = encounter.getView();
		batch.setProjectionMatrix(center_cam.combined);
		wipeScreen();
		drawBackground();
		drawPlayer();
		drawOpponent(encounter.getOpponentName());
		if (null != encounter.getTextbox()){
			drawDefaultTextbox(encounter.getTextbox());
		}
		else{
			drawButtons(view);
		}
		drawOverlay();
	}
	
	/**
	 * Draws the background behind Beings - a screenshot of where they were standing.
	 */
	private void drawBackground(){
		batch.begin();
		batch.setColor(1, 1, 1, 0.5f);
		batch.draw(background, 0, 0, background.getRegionWidth()/2, background.getRegionHeight()/2);
		batch.end();
		batch.setColor(DEFAULT_COLOR);
	}
	
	private void drawPlayer(){ // TODO: Load from player state
		batch.begin();
		batch.draw(
				playerImage, 
				closeness, 
				0
				);
		batch.end();
	}
	
	private void drawOpponent(String opponentID){ // TODO: Load from opponent ID and state
		batch.begin();
		batch.draw(
				opponentImage, 
				Gdx.graphics.getWidth() * GraphicsHandler.ZOOM - opponentImage.getRegionWidth() - closeness, 
				0
				);
		batch.end();
	}
	
	private void drawButtons(View view){
		for (Button button: view.getButtons()){
			drawText(
					button.getName(), 
					button.getArea().getPosition(new Vector2()), 
					button.getArea().getSize(new Vector2()).scl(1.0f/FrameEngine.TILE),
					true
					);
		}
	}
	
}

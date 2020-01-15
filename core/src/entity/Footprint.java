package entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import area.Area.Terrain;
import timer.DurationTimer;

public class Footprint extends ImmobileEntity {
	
	private TextureRegion tex = new TextureRegion(new Texture("sprites/player/footstep.png"));
	private final int duration = 600;
	private final DurationTimer life = new DurationTimer(duration);
	private final Color color;
	private final float startAlpha, angle;
	private final boolean onSlope;

	public Footprint(float x, float y, float angle, Terrain terrain, boolean onSlope) {
		super(x, y);
		image = new Sprite(tex);
		timerList.add(life);
		layer = Layer.NORMAL;
		
		this.angle = angle;
		switch(terrain){
		case NORMAL: startAlpha = 0.4f;
			break;
		case STONE: startAlpha = 0.25f;
			break;
		case ICE: startAlpha = 0.2f;
			break;
		case WATER: startAlpha = 0.1f;
			break;
		case WOOD: startAlpha = 0.15f;
			break;
		case SNOW: startAlpha = 0.55f;
			break;
		case DEEP_WATER: startAlpha = 0.0f;
			break;
		case SAND: startAlpha = 0.6f;
			break;
		default:{
			startAlpha = 1;
			System.out.println("Footstep encountered unknown terrain");
			break;
		}
		}
		color = new Color(1, 1, 1, startAlpha);
		this.onSlope = onSlope;
	}
	
	public void update(){
		super.update();
		color.a = Math.min(startAlpha, startAlpha * (life.getEndTime() - life.getCounter())/(duration/4.0f) );
		if (life.timeUp()){
			this.setRemove();
		}
	}
	
	@Override
	public Color getColor(){
		return color;
	}
	
	@Override
	public float getAngle(){
		return angle;
	}
	
	@Override
	public float getHeightMod() {
		return onSlope ? 0.85f : 1f;
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}

package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import timer.Timer;

public class Switch extends InteractableEntity {
	
	private boolean on = false;
	private Timer onTimer = new Timer(2);
	private final String id;
	private final Sound 
		onSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/switch_on.wav")),
		offSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/switch_off.wav"));
	
	private final TextureRegion 
		offTex = new TextureRegion(new Texture("sprites/objects/switch_off.png")), 
		onTex = new TextureRegion(new Texture("sprites/objects/switch_on.png"));

	public Switch(float x, float y, String id) {
		super(x, y, "");
		this.id = id;
		image = offTex;
		timerList.add(onTimer);
		hitbox.height = 24;
		interactHitbox.height = 23;
	}
	
	public void updateImage() {
		image = (on && onTimer.timeUp()) ? onTex : offTex;
	}
	
	public void interact() {
		on = !on;
		onTimer.reset();
		AudioHandler.playSoundVariedPitch(on ? onSFX : offSFX, 0.5f);
	}
	
	public String getID() {
		return id;
	}

	@Override
	public void dispose() {
		offTex.getTexture().dispose();
		onTex.getTexture().dispose();
		onSFX.dispose();
		offSFX.dispose();
	}

	public void turnOff() {
		on = false;
	}

	public boolean isOn() {
		return on;
	}

}

package entity;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.AudioHandler;
import main.EntityHandler;
import main.FrameEngine;
import timer.Timer;

public class Bumper extends Entity {
	
	private boolean goRight = true;
	private final float speed;
	private final Timer flipTimer = new Timer(20), pingTimer = new Timer(15), slideTimer = new Timer(15);
	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(15, "sprites/objects/bumper.png", 2, 1);
	private final Sound ping = Gdx.audio.newSound(Gdx.files.internal("sfx/ping.wav"));
	private final Sound slide = Gdx.audio.newSound(Gdx.files.internal("sfx/slide.wav"));

	public Bumper(float x, float y, float speed) {
		super(x, y);
		this.speed = (speed == 0) ? 3.2f : speed;
		timerList.addAll(Arrays.asList(flipTimer, pingTimer, slideTimer));
		image = anim.get(0).getKeyFrame(0);
		hitbox.height = image.getRegionHeight() - 4;
		slideTimer.change((int) (Math.random() * 10));
	}
	
	@Override
	public void update(){
		updateTimers();
		updateVelocity();
		updatePosition();
		updateImage();
		if (touchingPlayer(hitbox) && pingTimer.timeUp()) {
			FrameEngine.getPlayer().knock(this);
			AudioHandler.playSoundVariedPitch(ping, 0.65f);
			pingTimer.reset();
		}
		if (slideTimer.timeUp()) {
			AudioHandler.playPositionalSound(this, slide);
			slideTimer.reset();
		}
	}
	
	@Override
	public void updateVelocity() {
		velocity.x = goRight ? speed : -speed;
		if (flipTimer.timeUp()) {
			for (Entity en: EntityHandler.getEntities()) {
				if (en instanceof BumperBounce) {
					if (this.hitbox.overlaps(en.hitbox)) {
						flipTimer.reset();
						AudioHandler.playPositionalSound(this, ping, 0.04f);
						goRight = !goRight;
					}
				}
			}
		}
	}
	
	@Override
	public void updateImage() {
		image = anim.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(anim);
		ping.dispose();
		slide.dispose();
	}

}

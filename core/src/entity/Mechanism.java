package entity;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import main.AudioHandler;
import main.EntityHandler;
import main.FrameEngine;
import text.DialogueTree;

public class Mechanism extends InteractableEntity{
	
	private final Sound shutdownSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/switch_off.wav"));

	public Mechanism(float x, float y) {
		super(x, y, "");
		image = null;
		canInteract = false;
	}
	
	@Override
	public void update() {
		super.update();
		ArrayList<String> switches = new ArrayList<>();
		for (Entity en: EntityHandler.getEntities()) {
			if (en instanceof Switch) {
				Switch switchObj = (Switch)en;
				if (switchObj.isOn()) {
					switches.add(switchObj.getID());
				}
			}
		}
		if (switches.containsAll(Arrays.asList("LEFT", "MIDDLE", "RIGHT"))) {
			if (!FrameEngine.getSaveFile().getFlag("ROTATED")) {
				activate();
			}
		}
		else if (switches.containsAll(Arrays.asList("TOP", "MIDDLE", "BOTTOM"))) {
			if (FrameEngine.getSaveFile().getFlag("ROTATED")) {
				activate();
			}
		}
		if (switches.size() >= 3) {
			AudioHandler.playSoundVariedPitch(shutdownSFX);
			for (Entity en: EntityHandler.getEntities()) {
				if (en instanceof Switch) {
					Switch switchObj = (Switch)en;
					switchObj.turnOff();
				}
			}
		}
	}
	
	private void activate() {
		for (Entity en: EntityHandler.getEntities()) {
			if (en instanceof Dungeon) {
				Dungeon dungeon = (Dungeon)en;
				dungeon.rotate();
				FrameEngine.startDialogueTree(new DialogueTree(this, "mechanism"));
			}
		}
	}
	
	@Override
	public void dispose() {
		
	}

}

package entity;

public class Description extends InteractableEntity{

	public Description(float x, float y, float width, float height, String text) {
		super(x, y, text);
		voiceUrl = "blip";
		hitbox.setSize(0, 0);
		interactHitbox.setSize(width, height);
		image = null;
		position.x += width/2;
		position.y += height/2;
	}

	@Override
	public void dispose() {}

}

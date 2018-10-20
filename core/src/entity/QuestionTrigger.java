package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.QuestionHandler;

public class QuestionTrigger extends ImmobileEntity {

	public QuestionTrigger(float x, float y, float width, float height) {
		super(x, y);
		hitbox.setSize(width, height);
	}
	
	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			QuestionHandler.askQuestion();
			setRemove();
		}
	}
	
	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		/**/
	}

}

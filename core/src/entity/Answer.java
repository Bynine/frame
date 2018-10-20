package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import entity.Portal.Direction;
import main.FrameEngine;
import main.QuestionHandler;
import main.Question.AnswerDirection;

public class Answer extends ImmobileEntity {
	
	private final String id;

	public Answer(float x, float y, float width, float height, String id) {
		super(x, y);
		this.id = id;
		hitbox.setSize(width, height);
		updatePosition();
	}
	
	@Override
	public void update(){
		if (!FrameEngine.inTransition() && touchingPlayer(hitbox)){
			if (QuestionHandler.isCorrect(AnswerDirection.valueOf(id))){
				QuestionHandler.advance();
				FrameEngine.initiateAreaChange(QuestionHandler.nextArea(), 
						new Vector2(11.5f * FrameEngine.TILE, 16 * FrameEngine.TILE), Direction.UP);		
			}
			else{
				QuestionHandler.reset(true);
				FrameEngine.initiateAreaChange("UNDERSHRINE", 
						new Vector2(18 * FrameEngine.TILE, 21.5f * FrameEngine.TILE), Direction.DOWN);
			}
		}
		super.update();
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

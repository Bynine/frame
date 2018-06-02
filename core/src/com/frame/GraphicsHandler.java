package com.frame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import overworld.Entity;

public class GraphicsHandler {
	private static SpriteBatch batch;
	
	public static void initialize(){
		batch = new SpriteBatch();
	}
	
	public static void update(Entity en){
		Gdx.gl.glClearColor(0.35f, 0.48f, 0.47f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(en.getImage(), en.getPosition().x, en.getPosition().y);
		batch.end();
	}
}

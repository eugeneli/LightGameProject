package com.eli.lightgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LightGame implements ApplicationListener
{
	private SpriteBatch batch;
	
	private LightGameEngine Engine;
	
	@Override
	public void create()
	{	
		batch = new SpriteBatch();
		float width = Gdx.graphics.getWidth()/10;
		float height = Gdx.graphics.getHeight()/10;
		
		Engine = new LightGameEngine(batch, width, height);	
	}

	@Override
	public void dispose() {
		batch.dispose();
		Engine.dispose();
	}
	
	@Override
	public void render()
	{
		Engine.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}

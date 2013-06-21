package com.eli.lightgame.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.eli.lightgame.LightGameEngine;

public class LGMenu extends ApplicationAdapter
{
	protected Stage stage;
	protected LightGameEngine Engine;
	protected LGMenuHandler menuHandler;
	
	public LGMenu(LightGameEngine eng)
	{
		Engine = eng;
	}
	
	public void setHandler(LGMenuHandler handler)
	{
		menuHandler = handler;
	}
	
	public Stage getStage()
	{
		return stage;
	}
	
    @Override
    public void render()
    {
        stage.act();
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        Engine.render();
        stage.draw();
    }

    @Override public void resize(final int width, final int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}

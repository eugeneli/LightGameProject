package com.eli.lightgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eli.lightgame.ui.LGLevelSelect;
import com.eli.lightgame.ui.LGOptions;
import com.eli.lightgame.util.LGPreferences;

public class LightGame extends Game
{
	private SpriteBatch batch;

	private LGOptions options;
	private LGPreferences preferences;
	private LGLevelSelect levelSelect;
	private LightGameEngine Engine;
	
	public static GAMESTATE CURRENT_GAMESTATE;
	
	public static enum GAMESTATE{
		MAIN_MENU, LEVEL_SELECT, INGAME
	}
	
	@Override
	public void create()
	{	
		batch = new SpriteBatch();
		float width = Gdx.graphics.getWidth()/10;
		float height = Gdx.graphics.getHeight()/10;
		
		CURRENT_GAMESTATE = GAMESTATE.MAIN_MENU;
		
		preferences = new LGPreferences();
		
		Engine = new LightGameEngine(batch, width, height, true);	
		
		levelSelect = new LGLevelSelect(Engine);
		levelSelect.create();
		
		options = new LGOptions(Engine, preferences);
		options.create();
	}

	@Override
	public void dispose(){
		batch.dispose();
		Engine.dispose();
		levelSelect.dispose();
	}
	
	@Override
	public void render()
	{
		switch(CURRENT_GAMESTATE)
		{
			case MAIN_MENU:
				options.render();
				break;
			case LEVEL_SELECT:
				levelSelect.render();
				break;
			case INGAME:
				Engine.render();
				break;
		}
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

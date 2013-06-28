package com.eli.lightgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eli.lightgame.ui.LGLevelSelect;
import com.eli.lightgame.ui.LGMainMenu;
import com.eli.lightgame.ui.LGMenuHandler;
import com.eli.lightgame.ui.LGOptions;
import com.eli.lightgame.util.LGPreferences;

public class LightGame extends Game
{
	private SpriteBatch batch;

	private LGMenuHandler menuHandler;
	private LGPreferences preferences;
	private LGMainMenu mainMenu;
	private LGOptions options;
	private LGLevelSelect levelSelect;
	
	private LightGameEngine Engine;
	
	public static GAMESTATE CURRENT_GAMESTATE;
	
	public static enum GAMESTATE{
		IN_MENU, INGAME
	}
	
	@Override
	public void create()
	{	
		batch = new SpriteBatch();
		float width = Gdx.graphics.getWidth()/10;
		float height = Gdx.graphics.getHeight()/10;
		
		CURRENT_GAMESTATE = GAMESTATE.IN_MENU;
		
		preferences = new LGPreferences();
		Engine = new LightGameEngine(batch, width, height, preferences, true);	
		
		mainMenu = new LGMainMenu(Engine, batch);
		mainMenu.create();
		
		levelSelect = new LGLevelSelect(Engine, preferences);
		levelSelect.create();
		
		options = new LGOptions(Engine, preferences);
		options.create();
		
		menuHandler = new LGMenuHandler(mainMenu, levelSelect, options);
	}
	
	@Override
	public void dispose()
	{
		batch.dispose();
		Engine.dispose();
		menuHandler.dispose();
	}
	
	@Override
	public void render()
	{
		switch(CURRENT_GAMESTATE)
		{
			case IN_MENU:
				Gdx.input.setInputProcessor(menuHandler.getCurrentMenu().getStage());
				menuHandler.render();
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

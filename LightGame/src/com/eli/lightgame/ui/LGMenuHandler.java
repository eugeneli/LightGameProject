package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;

public class LGMenuHandler
{
	private LGMainMenu mainMenu;
	private LGLevelSelect levelSelectMenu;	
	private LGOptions optionsMenu;
	
	private LGMenu currentMenu;
	
	public static enum Menu{
		MAIN, OPTIONS, LEVEL_SELECT, NONE
	}
	
	public LGMenuHandler(LGMainMenu main, LGLevelSelect levelSel, LGOptions options)
	{
		mainMenu = main;
		levelSelectMenu = levelSel;
		optionsMenu = options;
		
		mainMenu.setHandler(this);
		levelSelectMenu.setHandler(this);
		optionsMenu.setHandler(this);
		
		currentMenu = mainMenu;
		Gdx.input.setInputProcessor(mainMenu.getStage());
	}
	
	public void setMenu(Menu menu)
	{
		switch(menu)
		{
			case MAIN:
				currentMenu = mainMenu;
				Gdx.input.setInputProcessor(mainMenu.getStage());
				break;
			case OPTIONS:
				currentMenu = optionsMenu;
				Gdx.input.setInputProcessor(optionsMenu.getStage());
				break;
			case LEVEL_SELECT:
				currentMenu = levelSelectMenu;
				Gdx.input.setInputProcessor(levelSelectMenu.getStage());
				break;
			case NONE:
				currentMenu = null;
				break;
		}
	}
	
	public LGMenu getCurrentMenu()
	{
		return currentMenu;
	}
	
	public void dispose()
	{
		mainMenu.dispose();
		optionsMenu.dispose();
		levelSelectMenu.dispose();
	}
	
	public void render()
	{
		currentMenu.render();
	}
}

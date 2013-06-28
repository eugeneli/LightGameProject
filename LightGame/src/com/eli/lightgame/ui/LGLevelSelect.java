package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.eli.lightgame.LightGame;
import com.eli.lightgame.LightGame.GAMESTATE;
import com.eli.lightgame.LightGameEngine;
import com.eli.lightgame.ui.LGMenuHandler.Menu;
import com.eli.lightgame.util.LGPreferences;
import com.esotericsoftware.tablelayout.Cell;

public class LGLevelSelect extends LGMenu
{
	private Table scrollTable;
	private LGPreferences preferences;
	
	public LGLevelSelect(LightGameEngine eng, LGPreferences pref)
	{
		super(eng);
		
		preferences = pref;
		stage = new Stage();
        scrollTable = new Table();
        
        BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/fonts/corbelsmall.fnt"), false);
//        /fontType.scale(1.5f);
        TextButtonStyle style = new TextButtonStyle();
        style.font = fontType;
        
        //Read level index and pass id to engine
        Json json = new Json();
		JsonValue levelIndex = json.fromJson(null, Gdx.files.internal("data/levels/levelindex.json"));
		
		JsonValue levels = levelIndex.get("Levels");
		for(int i = 0; i < levels.size; i++)
		{
			LGLevelSelectButton button = new LGLevelSelectButton("data/levels/level"+(i+1)+"/"+ (i+1) +".png");
			button.setName(levels.get(i).getString("id"));
			
			scrollTable.add(button).padBottom(10f);
			scrollTable.row();
			
			button.addListener(levelClickListener);
		}
	  /*{
		 	JsonValue levelData = levels.get(i);
			TextButton text = new TextButton(i + " ", style);
			if(i % 3 == 0)
			{
				if(i != 0)
					scrollTable.row();
				
				Cell cell = scrollTable.add(text);
				cell.colspan(3);
				
				
			}
			else
				scrollTable.add(text);
				
			
			//TextButton text = new TextButton(levelData.getString("Title"), style);
	        //text.setName(levelData.getString("id"));
	        //text.addListener(levelClickListener);

	        //Cell cell = scrollTable.add(text);
	        //cell.align(Align.left);
	        
	     //   scrollTable.row();
		}
		
        ScrollPane scroller = new ScrollPane(scrollTable);

        //scroller.setPosition(10, 10);
        scroller.setFillParent(true);

        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();*/
		ScrollPane scroller = new ScrollPane(scrollTable);
		
		scroller.setHeight(stage.getHeight()*0.85f);
		scroller.setWidth(stage.getWidth());
		scroller.setPosition(0, stage.getHeight()*.1f);
		
		TextButton backButton = new TextButton("Back", style);
		backButton.addListener(new ClickListener() {
    		@Override
    		public void clicked (InputEvent event, float x, float y)
    		{
    			menuHandler.setMenu(Menu.MAIN);
    		}
    	});
		backButton.setPosition(stage.getWidth()/2 - backButton.getWidth()/2, stage.getHeight()*0.04f);
        
        stage.addActor(scroller);
        stage.addActor(backButton);
	}
    
	public ClickListener levelClickListener = new ClickListener() {
		@Override
		public void clicked (InputEvent event, float x, float y)
		{
			Engine.dispose();
			Engine.initialize(false, Integer.parseInt(event.getListenerActor().getName()));
			//Engine.setOnScreenControls(preferences.useOnScreenControls());
			LightGame.CURRENT_GAMESTATE = GAMESTATE.INGAME;
		}
	};
}
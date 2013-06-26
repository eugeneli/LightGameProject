package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.eli.lightgame.util.LGPreferences;

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
        
        BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/default.fnt"), false);
//        /fontType.scale(1.5f);
        TextButtonStyle style = new TextButtonStyle();
        style.font = fontType;
        
        //Read level index and pass id to engine
        Json json = new Json();
		JsonValue levelIndex = json.fromJson(null, Gdx.files.internal("data/levels/levelindex.json"));
		
		JsonValue levels = levelIndex.get("Levels");
		for(int i = 0; i < levels.size; i++)
		{
			JsonValue levelData = levels.get(i);
			
			TextButton text = new TextButton(levelData.getString("Title"), style);
			text.align(Align.center);
	        text.setName(levelData.getString("id"));
	        text.addListener(levelClickListener);

	        scrollTable.add(text);
	        scrollTable.row();
		}

        final ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setFillParent(true);

/*        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();*/

        stage.addActor(scroller);
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
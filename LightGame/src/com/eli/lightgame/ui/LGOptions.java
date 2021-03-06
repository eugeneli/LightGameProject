package com.eli.lightgame.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

public class LGOptions extends LGMenu
{
	private Table table;
	private LGPreferences preferences;
	
	public LGOptions(LightGameEngine eng, LGPreferences pref)
	{
		super(eng);
		preferences = pref;
		
		table = new Table();
        stage = new Stage();
        
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        table = new Table( skin );
        table.setFillParent(true);
        final CheckBox soundEffectsCheckbox = new CheckBox("                                Sound", skin);
        soundEffectsCheckbox.setChecked(preferences.isSoundEffectsEnabled());
        soundEffectsCheckbox.addListener( new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
    		{
                boolean enabled = soundEffectsCheckbox.isChecked();
                preferences.setSoundEffectsEnabled( enabled );
            }
        } );
        
        table.add(soundEffectsCheckbox);
        table.row();

        final CheckBox musicCheckbox = new CheckBox("                                 Music", skin);
        musicCheckbox.setChecked(preferences.isMusicEnabled());
        musicCheckbox.addListener( new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
    		{
                preferences.setMusicEnabled(musicCheckbox.isChecked());
            }
        } );
        
        table.add(musicCheckbox);
        table.row();
        
    /*    final CheckBox oscCheckbox = new CheckBox("        Onscreen controls", skin);
        oscCheckbox.setChecked(preferences.useOnScreenControls());
        oscCheckbox.addListener( new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
    		{
                preferences.setOnScreenControls(oscCheckbox.isChecked());
            }
        } );

        table.add(oscCheckbox);
        table.row();*/
        
        BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/fonts/corbelsmall.fnt"), false);

        TextButtonStyle style = new TextButtonStyle();
        style.font = fontType;
        
        TextButton backButton = new TextButton("Back", style);
        backButton.addListener( new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
    		{
            	menuHandler.setMenu(Menu.MAIN);
            }
        } );
        
        table.add(backButton);
        table.row();
        
        stage.addActor(table);
        
        
       // stage.addActor(musicCheckbox);
     //   stage.addActor(backButton);
        
       /* scrollTable = new Table();
        
        BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/default.fnt"), false);
        fontType.scale(1.5f);
        TextButtonStyle style = new TextButtonStyle();
        style.font = fontType;
        
        //Read level index
        Json json = new Json();
		JsonValue levelIndex = json.fromJson(null, Gdx.files.internal("data/levels/levelindex.json"));
		
		JsonValue levels = levelIndex.get("Levels");
		for(int i = 0; i < levels.size; i++)
		{
			JsonValue levelData = levels.get(i);
			
			TextButton text = new TextButton(levelData.getString("Title"), style);
			text.align(Align.center);
	        text.setName(levelData.getString("Path"));
	        text.addListener(levelClickListener);

	        scrollTable.add(text);
	        scrollTable.row();
		}

        final ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setFillParent(true);

/*        final Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();

        this.stage.addActor(scroller);*/
	}
}
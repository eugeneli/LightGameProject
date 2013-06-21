package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.eli.lightgame.LightGameEngine;
import com.eli.lightgame.ui.LGMenuHandler.Menu;

public class LGMainMenu extends LGMenu
{
	private Sprite logo;
	private Table table;
	private SpriteBatch batch;
	
	public LGMainMenu(LightGameEngine eng, SpriteBatch sb)
	{
		super(eng);
		batch = sb;
		stage = new Stage();
		table = new Table();
		
		logo = new Sprite(new Texture(Gdx.files.internal("data/logo.png")));
		//logo.scale(-0.5f);
		
	    table = new Table(new Skin(Gdx.files.internal("data/uiskin.json")));
	    table.setFillParent(true);
        table.setPosition(240, -50);
        
        BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/default.fnt"), false);
//      fontType.scale(1.5f);
        TextButtonStyle style = new TextButtonStyle();
        style.font = fontType;
        
        TextButton startGame = new TextButton("Start Game", style);
        startGame.align(Align.center);
        startGame.addListener( new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
    		{
                menuHandler.setMenu(Menu.LEVEL_SELECT);
            }
        } );
        
        table.add(startGame);
        table.row();
        
        TextButton options = new TextButton("Options", style);
        options.align(Align.center);
        options.addListener( new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
    		{
            	menuHandler.setMenu(Menu.OPTIONS);
            }
        } );
        
        table.add(options);
        table.row();
       
        stage.addActor(table);
	}
	
	public void render()
	{
		super.render();
		batch.begin();
		logo.draw(batch);
		batch.end();
	}

}

package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	private Texture logo;
	private Table table;
	
	public LGMainMenu(LightGameEngine eng, SpriteBatch batch)
	{
		super(eng);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
		table = new Table();
		
		logo = new Texture(Gdx.files.internal("data/logo.png"));
		TextureRegion region = new TextureRegion(logo, 0, 0, 480, 154);
		
		Image logoActor = new Image(region);
		//actor.setPosition(stage.getWidth()-1.32f*logo.getWidth(), stage.getHeight()-2.3f*logo.getHeight());
		
	    table = new Table(new Skin(Gdx.files.internal("data/uiskin.json")));
	    table.setFillParent(true);
        table.setPosition(247, 40);
        
        table.add(logoActor);
        table.row();
        
        BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/fonts/corbel.fnt"), false);
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
}

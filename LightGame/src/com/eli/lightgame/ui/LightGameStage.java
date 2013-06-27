package com.eli.lightgame.ui;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.Level;
import com.eli.lightgame.LightGameEngine;

public class LightGameStage
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private LightGameEngine engine;
	private Level level;
	
	private Stage stage;
    private LGInput input;
    private Skin skin;
    
    private Label titleText;
	private Label missionText;
	private Label middleMessage;
	private Table table;
	
	private BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/fonts/corbelsmall.fnt"), false);
	private LabelStyle style = new LabelStyle();
	private Queue<String> tips = new LinkedList<String>();
    
    public LightGameStage(LightGameEngine eng, Level lev, OrthographicCamera cam, SpriteBatch sb, EntityHandler eh)
    {
    	camera = cam;
    	batch = sb;
    	engine = eng;
    	level = lev;
    	
    	skin = new Skin(Gdx.files.internal("data/uiskin.json"));
    	style.font = fontType;
    	
    	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
    	input = new LGInput(camera, stage, eh);
    	
    	stage.addListener(input);
    	
    	table = new Table(skin);
 	    table.setFillParent(true);
        table.setPosition(0, (stage.getHeight()/2)*0.8f);
        
        displayTitle(level.getTitleMessage()[0],level.getTitleMessage()[1]);
    }
    
    public void displayTitle(String title, String mission)
    {
    	BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/fonts/corbel.fnt"), false);
    	//fontType.scale(1.5f);
    	LabelStyle style = new LabelStyle();
        style.font = fontType;
      
    	titleText = new Label(title, skin);
    	missionText = new Label(mission, style);
    	
    	table.add(titleText);
    	table.row();
    	table.add(missionText);
    	table.row();
    	
    	table.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0, -10, 0.3f), Actions.fadeIn(0.3f)), Actions.delay(4f), Actions.parallel(Actions.moveBy(0, 10, 0.3f), Actions.fadeOut(0.3f))));
    	stage.addActor(table);
    }
    
    public void displayTips(Queue<String> tipsQueue)
    {
    	if(tips.size() == 0)
    		tips = tipsQueue;

        middleMessage = new Label(tips.remove(), style);
        middleMessage.setPosition(stage.getWidth()/2 - middleMessage.getWidth()/2, stage.getHeight()/2-50);
        middleMessage.addAction(Actions.sequence(Actions.fadeIn(0.3f), Actions.delay(4f), Actions.fadeOut(0.3f)));
    	
    	stage.addActor(middleMessage);
    	
    	stage.addListener(dissmissTipListener);
    }
    
    public void displayEndMenu()
    {
    	if(engine.hasNextLevel())
    	{
            middleMessage = new Label("Level Complete\n\nTap to continue", style);
            middleMessage.setPosition(stage.getWidth()/2 - middleMessage.getWidth()/2, stage.getHeight()/2-50);
            middleMessage.addAction(Actions.fadeIn(0.3f));
        	
        	stage.addActor(middleMessage);
        	
        	stage.addListener(levelEndListener);
    	}
    	else
    	{
    		
    	}
    }
    
    public void act(float delta)
    {
    	stage.act(Gdx.graphics.getDeltaTime());
    	
    	if (table.getActions().size == 0 && middleMessage == null)
    	{
    		if(level.getTips().size() > 0)
        		displayTips(level.getTips());
    	}
    }
    
    public void draw()
    {	
    	stage.draw();
    }
    
    public Stage getStage()
    {
    	return stage;
    }
    
    public void setZoomBounds(float min, float max, float def)
    {
    	input.setZoomBounds(min, max);
    	camera.zoom = def;
    }
    
    InputListener dissmissTipListener = new InputListener(){
    	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
    	{
    		middleMessage.clearActions();
    		middleMessage.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.delay(1f)));
    		//stage.removeListener(this);
    		if(tips.size() > 0)
    			displayTips(tips);
    			
    		return false;
    	}
    };
    
    InputListener levelEndListener = new InputListener(){
    	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
    	{
    		middleMessage.addAction(Actions.fadeOut(0.5f));
    		stage.removeListener(this);
    		engine.loadNextLevel();
    		return false;
    	}
    };
}
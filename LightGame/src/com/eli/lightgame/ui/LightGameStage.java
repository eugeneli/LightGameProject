package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.eli.lightgame.EntityHandler;

public class LightGameStage
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Stage stage;
    private LGInput input;
    private Skin skin;
    
    private Label titleText;
	private Label missionText;
	private Table table;
    
    public LightGameStage(OrthographicCamera cam, SpriteBatch sb, EntityHandler eh)
    {
    	camera = cam;
    	batch = sb;
    	
    	skin = new Skin(Gdx.files.internal("data/uiskin.json"));
    	
    	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
    	input = new LGInput(camera, stage, eh);
    	
    	stage.addListener(input);
    	
    	table = new Table(skin);
 	    table.setFillParent(true);
        table.setPosition(0, (stage.getHeight()/2)*0.8f);
    }
    
    public void displayTitle(String title, String mission)
    {
    	BitmapFont fontType = new BitmapFont(Gdx.files.internal("data/corbelsmall.fnt"), false);
    	//fontType.scale(1.5f);
    	LabelStyle style = new LabelStyle();
        style.font = fontType;
      
    	titleText = new Label(title, skin);
    	missionText = new Label(mission, style);
    	
    	table.add(titleText);
    	table.row();
    	table.add(missionText);
    	table.row();
    	
    	table.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0, -10, 0.3f), Actions.fadeIn(0.3f)), Actions.delay(2.8f), Actions.parallel(Actions.moveBy(0, 10, 0.3f), Actions.fadeOut(0.3f))));
    	stage.addActor(table);
    }
    
    public void displayEndMenu(int currentLevel)
    {
    	//Table tbStyle = new Tabl
    }
    
    public void act(float delta)
    {
    	stage.act(Gdx.graphics.getDeltaTime());
    }
    
    public void draw()
    {	
    	stage.draw();
    }
    
    public Stage getStage()
    {
    	return stage;
    }
    
    public void setZoomBounds(float min, float max)
    {
    	input.setZoomBounds(min, max);
    	camera.zoom = max;
    }
}

package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

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
    
   /* private ActorGestureListener zoom = new ActorGestureListener()
    {
    	public void zoom(InputEvent event, float initialDistance, float distance)
    	{
    		float scalar = ((initialDistance-distance)/initialDistance)/2;

            camera.zoom += scalar;
            if(camera.zoom <= minZoom)
                    camera.zoom = minZoom;
            if(camera.zoom >= maxZoom)
                    camera.zoom = maxZoom;
            camera.update();
    	}
    };*/
    
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
    	titleText = new Label(title, skin);
    	missionText = new Label(mission, skin);
    	
    	table.add(titleText);
    	table.row();
    	table.add(missionText);
    	table.row();
    	
    	//titleText.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f), Actions.delay(2f), Actions.fadeOut(1f)));
    	//missionText.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f), Actions.delay(2f), Actions.fadeOut(1f)));
    	
    	//stage.addActor(titleText);
    	//stage.addActor(missionText);
    	
    	table.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f), Actions.delay(2f), Actions.fadeOut(1f)));
    	stage.addActor(table);
    	
    /*	Sequence sequence = Sequence.$(
                MoveTo.$(200, 200, 0.5f), //move actor to 200,200
                RotateTo.$(90, 0.5f),     //rotate actor to 90°
                FadeOut.$(0.5f),          //fade out actor (change alpha to 0)
                Remove.$()                //remove actor from stage
              );
    	
    	Actions.fadeIn(duration);
    	
    	FadeIn fadeInAction = FadeIn.$(2f);
    	FadeOut fadeOutAction = FadeOut.$(2f);
    	Delay delayAction = Delay.$(fadeOutAction, 5f);
    	Sequence sAction = Sequence.$(fadeInAction, delayAction);*/
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

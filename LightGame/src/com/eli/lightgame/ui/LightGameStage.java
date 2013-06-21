package com.eli.lightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.entities.Player;

public class LightGameStage
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Stage stage;
	private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;
    
    private final Player player;
    private LGInput input;
    private boolean usingOnScreenControls;
    
    private ActorGestureListener zoom = new ActorGestureListener()
    {
    	public void zoom(InputEvent event, float initialDistance, float distance)
    	{
    		float scalar = ((initialDistance-distance)/initialDistance)/2;

            camera.zoom += scalar;
            if(camera.zoom <= 2.5f)
                    camera.zoom = 2.5f;
            if(camera.zoom >= 8.0f)
                    camera.zoom = 8.0f;
            camera.update();
    	}
    };
    
    public LightGameStage(String touchpadBackground, String touchpadKnob, OrthographicCamera cam, SpriteBatch sb, EntityHandler eh, boolean onScreenControls)
    {
    	camera = cam;
    	batch = sb;
    	player = eh.getPlayer();
    	usingOnScreenControls = onScreenControls;
    	
    	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
    	input = new LGInput(camera, stage, eh);
    	
		//Create a touchpad skin    
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture(touchpadBackground));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture(touchpadKnob));
        //Create TouchPad Style
        touchpadStyle = new TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(15, 15, 200, 200);

        if(usingOnScreenControls)
        {
        	//add TouchPad
            stage.addActor(touchpad);
            
            //Button to shoot bullets
            LGTriangleAttackButton triangleAttack = new LGTriangleAttackButton("data/buttonup.png", "data/buttondown.png", player);
            triangleAttack.setBounds(stage.getWidth()-triangleAttack.width, 15, triangleAttack.width, triangleAttack.height);
            stage.addActor(triangleAttack);
            
            //Add pinch to zoom listener
            stage.addListener(zoom);
        }
        else
        {
        	stage.addListener(input);
        	stage.addListener(input.getDragListener());
        }
    }
    
    public Stage getStage()
    {
    	return stage;
    }
    
    public Touchpad getTouchpad()
    {
    	return touchpad;
    }
}

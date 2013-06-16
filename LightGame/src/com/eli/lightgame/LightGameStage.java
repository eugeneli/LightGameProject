package com.eli.lightgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.eli.lightgame.entities.Player;
import com.eli.lightgame.ui.LGTriangleAttackButton;

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
    
    public LightGameStage(String touchpadBackground, String touchpadKnob, OrthographicCamera cam, SpriteBatch sb, Player p)
    {
    	camera = cam;
    	batch = sb;
    	player = p;
    	
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

        //Create a Stage and add TouchPad
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
        stage.addActor(touchpad);
        
        //Button to shoot bullets
        LGTriangleAttackButton triangleAttack = new LGTriangleAttackButton("data/buttonup.png", "data/buttondown.png", player);
        triangleAttack.setBounds(stage.getWidth()-triangleAttack.width, 15, triangleAttack.width, triangleAttack.height);
        stage.addActor(triangleAttack);
        
        //Add pinch to zoom listener
        stage.addListener(new ActorGestureListener() {
        	public void zoom(InputEvent event, float initialDistance, float distance) {
        		float scalar = ((initialDistance-distance)/initialDistance)/2;
                System.out.println(scalar);
                camera.zoom += scalar;
                if(camera.zoom <= 2.5f)
                        camera.zoom = 2.5f;
                if(camera.zoom >= 8.0f)
                        camera.zoom = 8.0f;
                camera.update();
                //System.out.println(camera.zoom);
        }
        });
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

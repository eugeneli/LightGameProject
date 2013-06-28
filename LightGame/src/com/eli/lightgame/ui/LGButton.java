package com.eli.lightgame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class LGButton extends Actor
{
	protected Sprite currentSprite;
	protected Sprite upSprite;
	protected Sprite downSprite;
	
	protected InputListener defaultInputListener;
	protected boolean tint = false;
	
	public float width;
	public float height;
	
	public LGButton(String upSpritePath, String downSpritePath)
	{
		upSprite = new Sprite(new Texture(upSpritePath));
		downSprite = new Sprite(new Texture(downSpritePath));
		currentSprite = upSprite;
		
		width = currentSprite.getWidth();
		height = currentSprite.getHeight();
		
		setTouchable(Touchable.enabled);
		setWidth(width);
		setHeight(height);
		
		defaultInputListener = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	currentSprite = downSprite;
                return true;  // must return true for touchUp event to occur
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	currentSprite = upSprite;
            }
        };
		
		addListener(defaultInputListener);
	}
	
	public void changeDefaultListener(InputListener listener)
	{
		defaultInputListener = listener;
	}
	
	@Override
    public void draw(SpriteBatch batch, float parentAlpha)
	{
		if(tint)
			batch.setColor(Color.GRAY);
		else
			batch.setColor(Color.WHITE);
        batch.draw(currentSprite, getX(), getY());
    }  
}

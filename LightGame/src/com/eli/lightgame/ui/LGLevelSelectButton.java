package com.eli.lightgame.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class LGLevelSelectButton extends LGButton
{
	public LGLevelSelectButton(String upSpritePath)
	{
		super(upSpritePath, upSpritePath);
		
		clearListeners();
		
		InputListener inputListener = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	tint = true;
                return true;  // must return true for touchUp event to occur
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	tint = false;
            }
		};
		
		defaultInputListener = inputListener;
		addListener(defaultInputListener);
	}
}

package com.eli.lightgame.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.eli.lightgame.Patterns;
import com.eli.lightgame.entities.Player;

public class LGAttackButton extends LGButton
{
	public LGAttackButton(String upSpritePath, String downSpritePath, final Player player)
	{
		super(upSpritePath, downSpritePath);
		
		clearListeners();
		
		InputListener inputListener = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	currentSprite = downSprite;
            	player.fire(1000);
                return true;  // must return true for touchUp event to occur
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	currentSprite = upSprite;
            }
		};
		
		defaultInputListener = inputListener;
		addListener(defaultInputListener);
	}
}

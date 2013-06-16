package com.eli.lightgame.entities;

import com.badlogic.gdx.graphics.Color;

public abstract class NPC extends Entity
{
	public NPC(String spritePath, Color aColor, float rad)
	{
		super(spritePath, aColor, rad);
	}

	@Override
	public void updateSizes() {
		// TODO Auto-generated method stub
		
	}
	
	public void update()
	{
		super.update();
		doAI();
	}
	
	public abstract void doAI();
}

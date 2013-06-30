package com.eli.lightgame.entities;

import com.badlogic.gdx.graphics.Color;

public class MassiveEntity extends Entity
{
	protected float gravityMagnitude;
	protected boolean canBlackHole = false;
	
	public MassiveEntity(String spritePath, Color aColor, float rad)
	{
		super(spritePath, aColor, rad);
	}
	
	public float getGravityMagnitude()
	{
		return gravityMagnitude;
	}
	
	public void setGravityMagnitude(float mag)
	{
		gravityMagnitude = mag;
	}
	
	public boolean canBlackHole()
	{
		return canBlackHole;
	}
	
	public void setCanBlackHole(boolean bool)
	{
		canBlackHole = bool;
	}

	@Override
	public void updateSizes() {
		//implemented in child classes
	}

}

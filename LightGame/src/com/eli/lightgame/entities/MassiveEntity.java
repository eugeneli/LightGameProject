package com.eli.lightgame.entities;

import com.badlogic.gdx.graphics.Color;

public class MassiveEntity extends Entity
{
	private float gravityMagnitude;
	
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

	@Override
	public void updateSizes() {
		//implemented in child classes
	}

}

package com.eli.lightgame.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import box2dLight.Light;

public class Bullet extends Entity
{
	private int life; //How long should the bullet be alive?
	private int originalLife;
	private float angle;
	private boolean startShrinking = false;
	
	private Entity source;
	
	public Bullet(Entity bulletSource, String spritePath, Color aColor, float rad, Body b, ArrayList<Light> pl, int laifu, float ang)
	{
		super(spritePath, b, pl, rad);
		source = bulletSource;
		originalLife = life = laifu;
		angle = ang;
		color = aColor;
		
		canChangeColor = false;
		canChangeSize = false;
	}
	
	@Override
	public void updateSizes()
	{
		size = 2 * radius;
		lightSize = 3.5f * radius;
		
		//increase the distance of the lights based on entity's size
		for(Light aLight : lights)
		{
			aLight.setDistance(lightSize);
		}
	}
	
	public void move(Vector2 force)
	{
		entityBody.setLinearVelocity(force);
	}
	
	public void setDying(boolean isDying)
	{
		startShrinking = isDying;
	}
	
	public void kill()
	{
		life = 0;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public void decrementLife()
	{
		life--;
	}
	
	public Body getBody()
	{
		return entityBody;
	}
	
	public ArrayList<Light> getLights()
	{
		return lights;
	}
	
	public float getAngle()
	{
		return angle;
	}
	
	public float getAngleX()
	{
		return (float) Math.cos(angle);
	}
	
	public float getAngleY()
	{
		return (float) Math.sin(angle);
	}
	
	public Entity getSource()
	{
		return source;
	}
	
	public void update()
	{
		if(startShrinking)
		{
			radius -= 0.5f;
			updateSizes();
			
			if(radius <= 0)
				life = 0;
			
			super.update();
		}
		else if(life <= 0.65f * originalLife)
		{
			startShrinking = true;
			super.update();
		}
		else
			super.update();
	}
}

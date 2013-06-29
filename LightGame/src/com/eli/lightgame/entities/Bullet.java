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
	private boolean immortal = false;
	private Entity target;
	
	public Bullet(String spritePath, Color aColor, float rad, Body b, ArrayList<Light> pl, int laifu, float ang)
	{
		super(spritePath, b, pl, rad);
		originalLife = life = laifu;
		angle = ang;
		color = aColor;
		immortal = true;
		
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
	
	public boolean isImmortal()
	{
		return immortal;
	}
	
	public void setImmortal(boolean bool)
	{
		immortal = bool;
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
	
	public void setTarget(Entity bulTarget)
	{
		target = bulTarget;
	}
	
	public void update()
	{
		super.update();
		
		/*if(startShrinking)
		{
			radius -= 0.5f;
			updateSizes();
			
			if(radius <= 0)
				life = 0;
		}
		else if(life <= 0.65f * originalLife)
		{
			startShrinking = true;
		}*/
		
		if(target != null)
		{
			float toTargetX = (float)((target.getPosition().x - getPosition().x));
			float toTargetY = (float)((target.getPosition().y - getPosition().y));
			float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
			
			entityBody.setTransform(entityBody.getPosition(), rotAngle);
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(entityBody.getAngle()) * (500)),(float)(Math.sin(entityBody.getAngle()) * (500))));
		}
		
		
		//flicker the core's light
		float currentCoreLightDistance = lights.get(0).getDistance();
		
		if(currentCoreLightDistance <= radius*10)
		{
			dimCoreLight = false;
		}
		else if(currentCoreLightDistance >= radius*50) //5 * 10 because /10 shooter radius and /5 shooter radius
		{
			dimCoreLight = true;
		}
		
		if(dimCoreLight)
		{
			lights.get(0).setDistance(currentCoreLightDistance-flickerRate);
			
		}
		else
		{
			lights.get(0).setDistance(currentCoreLightDistance+flickerRate);
			
		}
	}
}

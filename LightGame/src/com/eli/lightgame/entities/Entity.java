package com.eli.lightgame.entities;

import java.util.ArrayList;

import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.eli.lightgame.AudioHandler;
import com.eli.lightgame.BulletHandler;

public abstract class Entity
{
	protected BulletHandler bulletHandler;
	
	protected Sprite sprite;
	protected Body entityBody;
	protected Color color;
	protected float radius;
	protected float size;
	protected float lightSize;
	protected float criticalRadius; //Radius when explosion occurs
	
	private float growthRate;
	private float maxGrowingRadius; //Puts a cap on how much a bullet can increase entity's size
	private boolean growing = false;
	
	protected boolean canChangeColor;
	protected boolean canChangeSize;
	
	protected boolean waitingToUpdateSize = false; //Can't change Box2D fixture while it's updating so set flag to change the fixture after it's done
	protected boolean waitingToBeDeleted = false;
	
	protected boolean ignoreSize = false;
	protected boolean ignoreExistence = false; //Used for win conditions
	
	public long lastTimeFired = 0;
	
	//Array to hold the lights
	protected ArrayList<Light> lights = new ArrayList<Light>();
	protected float flickerRate = 0.1f;
	protected boolean dimCoreLight = false;
	
	public Entity(String spritePath, Color aColor, float rad)
	{
		sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
		color = aColor;
		
		radius = rad;
		size = 2 * radius;

		sprite.setSize(size, size);
	}
	
	@SuppressWarnings("unchecked")
	public Entity(String spritePath, Body b, ArrayList<Light> pl,  float rad)
	{
		sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));	
		
		lights = (ArrayList<Light>) pl.clone();
		
		entityBody = b;
		radius = rad;
		size = 2 * radius;
		
		sprite.setSize(size, size);
		sprite.setRotation(entityBody.getAngle());
		//sprite.setOrigin(getMiddleOfSprite().x, getMiddleOfSprite().y);
	}
	
	//Call this when changing the radius of the body
	public abstract void updateSizes();
	
	
	public void setFlickerRate(float flicker)
	{
		flickerRate = flicker;
	}
	
	public void isWaitingToUpdateSize(boolean resize)
	{
		waitingToUpdateSize = resize;
	}
	
	public boolean waitingToUpdateSize()
	{
		return waitingToUpdateSize;
	}
	
	public boolean waitingToBeDeleted()
	{
		return waitingToBeDeleted;
	}
	
	public void toBeDeleted(boolean delete)
	{
		waitingToBeDeleted = delete;
	}
	
	public boolean ignoreSize()
	{
		return ignoreSize;
	}
	
	public boolean ignoreExistence()
	{
		return ignoreExistence;
	}
	
	public Body getBody()
	{
		return entityBody;
	}
	
	public float getCritRadius()
	{
		return criticalRadius;
	}
	
	public void setCritRadius(float rad)
	{
		criticalRadius = rad;
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	public void setRadius(float rad)
	{
		radius = rad;
		updateSizes();
	}
	
	public void addToRadius(float rad)
	{
		radius += rad;
		updateSizes();
	}
	
	public void grow(float targetRadius, float growingRate)
	{
		growthRate = growingRate;
		maxGrowingRadius = targetRadius;
		growing = true;
	}

	public Vector2 getMiddleOfSprite()
	{
		Rectangle boundingRect = sprite.getBoundingRectangle();
		float midX = (boundingRect.width)/2;
		float midY = (boundingRect.height)/2;
		return new Vector2(midX, midY);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color c)
	{
		color = c;
		
		for(Light light : lights)
		{
			if(!light.equals(Color.CYAN))
				light.setColor(color);
		}
	}
	
	public boolean canChangeColor()
	{
		return canChangeColor;
	}
	
	public void setCanChangeColor(boolean bool)
	{
		canChangeColor = bool;
	}
	
	public boolean canChangeSize()
	{
		return canChangeSize;
	}
	
	public Vector2 getPosition()
	{
		return entityBody.getPosition();
	}
	
	public float getMass()
	{
		return entityBody.getMass();
	}
	
	public void setID(int setID)
	{
		entityBody.setUserData(setID);
	}
	
	public int getID()
	{
		return (Integer)(entityBody.getUserData());
	}
	
	public void explode()
	{
		bulletHandler.queueExplosionBullet(radius/2, color, getPosition().x, getPosition().y, (int) (2 * radius), 0f);
		bulletHandler.queueExplosionBullet(radius/2, color, getPosition().x, getPosition().y, (int) (2 * radius), 5.0f);
		bulletHandler.queueExplosionBullet(radius/2, color, getPosition().x, getPosition().y, (int) (2 * radius), 10.0f);
		bulletHandler.queueExplosionBullet(radius/2, color, getPosition().x, getPosition().y, (int) (2 * radius), 15f);
		
		radius = 0;
		toBeDeleted(true);
		updateSizes();
		
		AudioHandler.getInstance().playExplosion();
	}
	
	public void dispose()
	{
		for(Light light : lights)
		{
			light.setActive(false);
		}
	}
	
	public void update()
	{
		if(growing && radius < maxGrowingRadius)
		{
			//setRadius(maxGrowingRadius);
			addToRadius(growthRate);
		}
		else
		{
			growing = false;
			maxGrowingRadius = radius;
		}
		
		sprite.setPosition(entityBody.getPosition().x - radius, entityBody.getPosition().y - radius); //Make sure the sprite's drawn where the physical body is
		sprite.setOrigin(getMiddleOfSprite().x, getMiddleOfSprite().y);
		//sprite.setRotation(entityBody.getAngle());
	}
	
	public void draw(SpriteBatch batch)
	{
		//sprite.draw(batch);
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), (float)Math.toDegrees(entityBody.getAngle()));
	}
}

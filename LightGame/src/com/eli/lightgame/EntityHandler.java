package com.eli.lightgame;


import java.util.HashMap;
import java.util.Iterator;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.Drifter;
import com.eli.lightgame.entities.Entity;
import com.eli.lightgame.entities.Player;
import com.eli.lightgame.entities.RedGiant;

public class EntityHandler
{
	private int currentEntityID = 0;
	private World world;
	private RayHandler rayHandler;
	private BulletHandler bulletHandler;
	
	private Player player;
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	
	private final float COLOR_CHANGE_RATE = 0.1f;
	
	public static enum EntityType{
		PLAYER, RED_GIANT, DRIFTER
	}
	
	public EntityHandler(World w, RayHandler rh, BulletHandler bh, float theWidth, float theHeight)
	{
		world = w;
		rayHandler = rh;
		bulletHandler = bh;
	}
	
	public Entity createEntity(EntityType type, Color aColor, float radius, float xPos, float yPos, float facingDirection, float velocity)
	{
		switch(type)
		{
			case PLAYER:
				player = new Player(world, rayHandler, bulletHandler, aColor, radius, xPos, yPos);
				player.setID(currentEntityID);
				entities.put(currentEntityID, player);
				currentEntityID++;
				return player;
			case RED_GIANT:
				RedGiant rg = new RedGiant(world, rayHandler, aColor, radius, 1.5f*radius, xPos, yPos);
				rg.setID(currentEntityID);
				entities.put(currentEntityID, rg);
				currentEntityID++;
				return rg;
			case DRIFTER:
				Drifter dr = new Drifter(world, rayHandler, aColor, radius, 1.5f*radius, xPos, yPos, facingDirection, velocity);
				dr.setID(currentEntityID);
				entities.put(currentEntityID, dr);
				currentEntityID++;
				return dr;
			default:
				return null;
		}
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public HashMap<Integer, Entity> getEntities()
	{
		return entities;
	}
	
	public void changeColor(Entity entity, Color bulletColor)
	{
		if(entity.getColor().equals(Color.WHITE))
		{
			entity.setColor(bulletColor);
		}
		else
		{
			System.out.println(bulletColor);
			float r = entity.getColor().r;
			float g = entity.getColor().g;
			float b = entity.getColor().b;
			float a = entity.getColor().a;
			
			if(bulletColor.r > 
			r)
				r += COLOR_CHANGE_RATE;
			else if(bulletColor.r < r)
				r -= COLOR_CHANGE_RATE;
			
			if(bulletColor.g > g)
				g += COLOR_CHANGE_RATE;
			else if(bulletColor.g < g)
				g -= COLOR_CHANGE_RATE;
			
			if(bulletColor.b > entity.getColor().b)
				b += COLOR_CHANGE_RATE;
			else if(bulletColor.b < entity.getColor().b)
				b -= COLOR_CHANGE_RATE;
			
			entity.setColor(new Color(r,g,b,a));
		}
	}
	
	public void changeSize(Entity entity, float newRad)
	{
		entity.setRadius(entity.getRadius()+newRad/2); //Absorb the bullet
		entity.updateSizes();
	}
	
	public void collideWithBullet(int entityID, Bullet aBullet)
	{
		Entity entity = entities.get(entityID);
		
		if(entity.canChangeColor())
			changeColor(entity, aBullet.getColor());
		
		if(entity.canChangeSize())
			changeSize(entity, aBullet.getRadius());
		
		aBullet.setDying(true);
	}
		
	public void update()
	{
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	Entity entity = entities.get(entityID);
	    	
	    	entity.update();
	    	
	    	//Update Box2D sizes
	    	if(entity.waitingToUpdateSize())
	    	{
	    		entity.getBody().destroyFixture(entity.getBody().getFixtureList().get(0));
				
				CircleShape newShape = new CircleShape();
				newShape.setRadius(entity.getRadius());
				
				FixtureDef circleFixture = new FixtureDef();
				circleFixture.shape = newShape;
				circleFixture.density = 1.0f;
				circleFixture.friction = 1.0f;
				circleFixture.restitution = 0.0f;
				circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_ENEMY;
				circleFixture.filter.maskBits = LightGameFilters.MASK_ENEMY;
				
				entity.getBody().createFixture(circleFixture);
	    	}
	    }
	}
	
	public void draw(SpriteBatch batch)
	{
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	entities.get(entityID).draw(batch);
	    }
	}
	
	public void updateAndDraw(SpriteBatch batch)
	{
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	entities.get(entityID).update();
	    	entities.get(entityID).draw(batch);
	    }
	}
}

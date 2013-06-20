package com.eli.lightgame;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.Drifter;
import com.eli.lightgame.entities.Entity;
import com.eli.lightgame.entities.LightCore;
import com.eli.lightgame.entities.Player;
import com.eli.lightgame.entities.RedGiant;
import com.sun.jmx.remote.internal.ArrayQueue;

public class EntityHandler
{
	private class EntityDefinition
	{
		public Color color;
		public float radius;
		public Vector2 position;
	}
	
	private int currentEntityID = 0;
	private World world;
	private RayHandler rayHandler;
	private BulletHandler bulletHandler;
	
	private Player player;
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	
	private final float COLOR_CHANGE_RATE = 0.1f;
	private final float SIZE_CHANGE_RATE = 0.5f;
	
	//Can't create new bodies until box2d is done so need to queue
	private Queue<EntityDefinition> queuedEntities = new LinkedList<EntityDefinition>();
	
	public static enum EntityType{
		PLAYER, RED_GIANT, DRIFTER, CORE
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
				player = new Player(world, rayHandler, bulletHandler, aColor, radius, 1.5f*radius, xPos, yPos);
				player.setID(currentEntityID);
				entities.put(currentEntityID, player); //Player always has ID of 0
				currentEntityID++;
				return player;
			case RED_GIANT:
				RedGiant rg = new RedGiant(world, rayHandler, bulletHandler, aColor, radius, 1.3f*radius, xPos, yPos);
				rg.setID(currentEntityID);
				entities.put(currentEntityID, rg);
				currentEntityID++;
				return rg;
			case DRIFTER:
				Drifter dr = new Drifter(world, rayHandler, bulletHandler, aColor, radius, 1.1f*radius, xPos, yPos, facingDirection, velocity);
				dr.setID(currentEntityID);
				entities.put(currentEntityID, dr);
				currentEntityID++;
				return dr;
			case CORE:
				LightCore core = new LightCore(world, rayHandler, bulletHandler, aColor, radius, 10f*radius, xPos, yPos, facingDirection, velocity);
				core.setID(currentEntityID);
				entities.put(currentEntityID, core);
				//System.out.println("id: " + currentEntityID);
				currentEntityID++;
				return core;
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
	
	public boolean isTargetingEntity(float x, float y)
	{
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	Entity entity = entities.get(entityID);
	    	
	    	//Hard to target small bodies so I did a nasty stupid hack :(
	    	Fixture fixture = entity.getBody().getFixtureList().get(0);
	    	if(fixture.testPoint(x, y))
	    		return true;
	    	
	    	if(fixture.testPoint(x+5, y+5))
	    		return true;
	    	
	    	if(fixture.testPoint(x-5, y-5))
	    		return true;
	    	
	    	if(fixture.testPoint(x-5, y))
	    		return true;
	    	
	    	if(fixture.testPoint(x+5, y))
	    		return true;
	    	
	    	if(fixture.testPoint(x, y-5))
	    		return true;
	    	
	    	if(fixture.testPoint(x, y+5))
	    		return true;
	    }
		return false;
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
			
			if(bulletColor.r > r)
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
		entity.setRadius(entity.getRadius()+newRad); //Absorb the bullet
		entity.updateSizes();
	}
	
	public void collideWithBullet(int entityID, Bullet aBullet)
	{
		Entity entity = entities.get(entityID);
		/*System.out.println("Current radius: " + entity.getRadius());
		System.out.println("Bul Radius:" + aBullet.getRadius());
		System.out.println("New radius: " + (float)(entity.getRadius()+aBullet.getRadius()));*/
		entity.grow(entity.getRadius()+aBullet.getRadius(), aBullet.getRadius()/20);
		
		aBullet.kill();
		
		//if(entity.canChangeColor())
		//	changeColor(entity, aBullet.getColor());
		
		//if(entity.canChangeSize())
			//changeSize(entity, aBullet.getRadius());
		
		//aBullet.setDying(true);
		//aBullet.kill();
	}
	
	public void collideNPCs(int firstEnt, int secondEnt)
	{
		Entity firstEntity = entities.get(firstEnt);
		Entity secondEntity = entities.get(secondEnt);
		
		//Collision between two NPCs
		//First check if is collision with a core
		if(firstEntity instanceof LightCore)
		{
			if(secondEntity.canChangeColor())
			{
				secondEntity.setColor(firstEntity.getColor());
			}
			removeEntity(firstEntity);
		}
		else if(secondEntity instanceof LightCore)
		{
			if(firstEntity.canChangeColor())
			{
				firstEntity.setColor(secondEntity.getColor());
			}
			removeEntity(secondEntity);
		}
		else if(firstEntity.getColor().equals(secondEntity.getColor())) //Bigger entity absorbs smaller one if they are same color
		{
			if(firstEntity.getRadius() > secondEntity.getRadius())
			{
				float tmpRadius = secondEntity.getRadius()-SIZE_CHANGE_RATE;
				if(tmpRadius <= 1)
				{
					firstEntity.addToRadius(SIZE_CHANGE_RATE);
					removeEntity(secondEntity);
				}
				else
				{
					firstEntity.addToRadius(SIZE_CHANGE_RATE);
					secondEntity.addToRadius(-SIZE_CHANGE_RATE);
				
					if(secondEntity.getRadius() <= 1)
						removeEntity(secondEntity);
				}
			}
			else
			{
				float tmpRadius = secondEntity.getRadius()-SIZE_CHANGE_RATE;
				if(tmpRadius <= 1)
				{
					secondEntity.addToRadius(SIZE_CHANGE_RATE);
					removeEntity(firstEntity);
				}
				else
				{
					secondEntity.addToRadius(SIZE_CHANGE_RATE);
					firstEntity.addToRadius(-SIZE_CHANGE_RATE);
					
					if(firstEntity.getRadius() <= 1)
						removeEntity(firstEntity);
				}
			}
		}
	}
	
	public void removeEntity(Entity entity)
	{
		entity.toBeDeleted(true);
	}
	
	public void explodeEntity(Entity entity)
	{
		//createEntity(EntityType.CORE, entity.getColor(), entity.getRadius(), entity.getPosition().x, entity.getPosition().y, 0f, 0f);
		EntityDefinition ed = new EntityDefinition();
		ed.color = entity.getColor();
		ed.radius = entity.getRadius();
		ed.position = entity.getPosition();
	
		queuedEntities.add(ed);
		
		entity.explode();
	}
		
	public void update()
	{
		if(queuedEntities.size() > 0)
    	{
    		EntityDefinition ed = queuedEntities.remove();
    		createEntity(EntityType.CORE, ed.color, ed.radius, ed.position.x, ed.position.y, 0f, 0f);
    	}
		
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	Entity entity = entities.get(entityID);
	    	
	    	entity.update();
	    	
	    	if(entity.getRadius() >= entity.getCritRadius())
	    	{
	    		explodeEntity(entity);
	    	}
	    	
	    	//Update Box2D sizes
	    	if(entity.waitingToUpdateSize())
	    	{
	    		if(entity.getRadius() >= 1)
	    		{
	    			entity.getBody().destroyFixture(entity.getBody().getFixtureList().get(0));
					
					CircleShape newShape = new CircleShape();
					newShape.setRadius(entity.getRadius());
					
					FixtureDef circleFixture = new FixtureDef();
					circleFixture.shape = newShape;
					circleFixture.density = 1.0f;
					circleFixture.friction = 1.0f;
					circleFixture.restitution = 0.0f;
					
					entity.getBody().createFixture(circleFixture);
					
					entity.isWaitingToUpdateSize(false);
	    		}
	    		else
	    			removeEntity(entity);
	    	}

	    	if(entity.waitingToBeDeleted())
	    	{
	    		entity.dispose();
	    		world.destroyBody(entity.getBody());
	    		
	    		it.remove();
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
	
	public void dispose()
	{
		Iterator<Integer> it = entities.keySet().iterator();
		while (it.hasNext())
	    {
	    	entities.get((Integer)it.next()).dispose();
	    }
		
		entities = null;
	}
}

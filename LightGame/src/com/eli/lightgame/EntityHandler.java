package com.eli.lightgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.entities.BlackHole;
import com.eli.lightgame.entities.Blinker;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.Chaser;
import com.eli.lightgame.entities.Drifter;
import com.eli.lightgame.entities.Entity;
import com.eli.lightgame.entities.Giant;
import com.eli.lightgame.entities.GiantBoss;
import com.eli.lightgame.entities.LightCore;
import com.eli.lightgame.entities.MassiveEntity;
import com.eli.lightgame.entities.Player;
import com.eli.lightgame.util.LGMath;

public class EntityHandler
{
	private class EntityDefinition
	{
		public EntityType type;
		public Color color;
		public float radius;
		public Vector2 position;
		
		public Vector2 velocity = null;
		public float facingDirection = 0f;
	}
	
	private int currentEntityID = 0;
	private World world;
	private RayHandler rayHandler;
	private BulletHandler bulletHandler;
	
	private Player player;
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	private ArrayList<MassiveEntity> gravityEntities = new ArrayList<MassiveEntity>(); //Entities that give off gravity. Remove from here also if removing from entities.
	
	private final float COLOR_CHANGE_RATE = 0.1f;
	private final float SIZE_CHANGE_RATE = 0.5f;
	
	//Can't create new bodies until box2d is done so need to queue
	private Queue<EntityDefinition> queuedEntities = new LinkedList<EntityDefinition>();
	
	public static enum EntityType{
		PLAYER, GIANT, DRIFTER, CORE, BLINKER, CHASER, BLACKHOLE, GIANTBOSS
	}
	
	public EntityHandler(World w, RayHandler rh, BulletHandler bh, float theWidth, float theHeight)
	{
		world = w;
		rayHandler = rh;
		bulletHandler = bh;
		bulletHandler.setEntityHandler(this);
	}
	
	public Entity createEntity(EntityType type, Color aColor, float radius, float critRadiusMult, float xPos, float yPos, float facingDirection, float velocity)
	{
		return createEntity(type, aColor, radius, critRadiusMult, xPos, yPos, facingDirection, velocity, 0, null);
	}
	
	public Entity createEntity(EntityType type, Color aColor, float radius, float critRadiusMult, float xPos, float yPos, float facingDirection, float velocity, float angularVel)
	{
		return createEntity(type, aColor, radius, critRadiusMult, xPos, yPos, facingDirection, velocity, angularVel, null);
	}
	
	public Entity createEntity(EntityType type, Color aColor, float radius, float critRadiusMult, float xPos, float yPos, float facingDirection, float velocity, float angularVel, String particlePath)
	{
		return createEntity(type, aColor, radius, critRadiusMult, xPos, yPos, facingDirection, velocity, angularVel, particlePath, true);
	}

	public Entity createEntity(EntityType type, Color aColor, float radius, float critRadiusMult, float xPos, float yPos, float facingDirection, float velocity, float angularVel, String particlePath, boolean isDynamic)
	{
		switch(type)
		{
			case PLAYER:
				player = new Player(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos);
				player.setID(currentEntityID);
				entities.put(currentEntityID, player); //Player always has ID of 0
				currentEntityID++;
				return player;
			case GIANT:
				Giant giant = new Giant(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, particlePath, this, facingDirection, velocity, angularVel, isDynamic);
				giant.setID(currentEntityID);
				giant.setGravityMagnitude(2500); //100000 is kinda high. So is 5000
				entities.put(currentEntityID, giant);
				gravityEntities.add(giant);
				currentEntityID++;
				return giant;
			case GIANTBOSS:
				GiantBoss giantBoss = new GiantBoss(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, particlePath, this, facingDirection, velocity, angularVel, isDynamic);
				giantBoss.setID(currentEntityID);
				giantBoss.setGravityMagnitude(5000); //100000 is kinda high. So is 5000
				entities.put(currentEntityID, giantBoss);
				gravityEntities.add(giantBoss);
				currentEntityID++;
				return giantBoss;
			case DRIFTER:
				Drifter dr = new Drifter(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, facingDirection, velocity, angularVel);
				dr.setID(currentEntityID);
				entities.put(currentEntityID, dr);
				currentEntityID++;
				return dr;
			case CORE:
				LightCore core = new LightCore(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, facingDirection, velocity);
				core.setID(currentEntityID);
				entities.put(currentEntityID, core);
				currentEntityID++;
				return core;
			case BLINKER:
				Blinker blinker = new Blinker(world, rayHandler, bulletHandler, aColor, radius, xPos, yPos);
				blinker.setID(currentEntityID);
				entities.put(currentEntityID, blinker);
				currentEntityID++;
				return blinker;
			case CHASER:
				Chaser chaser = new Chaser(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, facingDirection, velocity);
				chaser.setID(currentEntityID);
				entities.put(currentEntityID, chaser);
				currentEntityID++;
				return chaser;
			case BLACKHOLE:
				BlackHole bh = null;
				if(aColor.equals(Color.RED))
					bh = new BlackHole(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, "data/particleeffects/redhole.p", facingDirection, velocity);
				else if(aColor.equals(Color.BLUE))
					bh = new BlackHole(world, rayHandler, bulletHandler, aColor, radius, critRadiusMult*radius, xPos, yPos, "data/particleeffects/bluehole.p", facingDirection, velocity);
				
				bh.setID(currentEntityID);
				bh.setGravityMagnitude(5000);
				entities.put(currentEntityID, bh);
				gravityEntities.add(bh);
				currentEntityID++;
				return bh;
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
	
	public ArrayList<MassiveEntity> getGravityEntities()
	{
		return gravityEntities;
	}
	
	public Entity targetingEntity(float x, float y)
	{
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	Entity entity = entities.get(entityID);
	    	
	    	//Hard to target small bodies so I did a nasty stupid hack :(
	    	Fixture fixture = entity.getBody().getFixtureList().get(0);
	    	if(fixture.testPoint(x, y))
	    		return entity;
	    	
	    	if(fixture.testPoint(x+5, y+5))
	    		return entity;
	    	
	    	if(fixture.testPoint(x-5, y-5))
	    		return entity;
	    	
	    	if(fixture.testPoint(x-5, y))
	    		return entity;
	    	
	    	if(fixture.testPoint(x+5, y))
	    		return entity;
	    	
	    	if(fixture.testPoint(x, y-5))
	    		return entity;
	    	
	    	if(fixture.testPoint(x, y+5))
	    		return entity;
	    }
		return null;
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
		entity.grow(entity.getRadius()+aBullet.getWorth(), aBullet.getWorth()/20);
		
		aBullet.kill();
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
		else if(firstEntity.getColor().equals(secondEntity.getColor()) || firstEntity instanceof MassiveEntity || secondEntity instanceof MassiveEntity)
		{
			if((firstEntity.getRadius() > secondEntity.getRadius() && !(secondEntity instanceof BlackHole))//Non blackholes can't absorb blackholes
			    || (firstEntity.getRadius() > secondEntity.getRadius() && firstEntity instanceof BlackHole && secondEntity instanceof BlackHole)) 
			{
				float tmpRadius = secondEntity.getRadius()-SIZE_CHANGE_RATE;
				if(tmpRadius <= 2)
				{
					firstEntity.addToRadius(SIZE_CHANGE_RATE);
					removeEntity(secondEntity);
				}
				else
				{
					firstEntity.addToRadius(SIZE_CHANGE_RATE);
					secondEntity.addToRadius(-SIZE_CHANGE_RATE);
				
					if(secondEntity.getRadius() <= 2)
						removeEntity(secondEntity);
				}
			}
			else if((secondEntity.getRadius() > firstEntity.getRadius() && !(firstEntity instanceof BlackHole))
					|| (secondEntity.getRadius() > firstEntity.getRadius() && firstEntity instanceof BlackHole && secondEntity instanceof BlackHole)) 
			{
				float tmpRadius = secondEntity.getRadius()-SIZE_CHANGE_RATE;
				if(tmpRadius <= 2)
				{
					secondEntity.addToRadius(SIZE_CHANGE_RATE);
					removeEntity(firstEntity);
				}
				else
				{
					secondEntity.addToRadius(SIZE_CHANGE_RATE);
					firstEntity.addToRadius(-SIZE_CHANGE_RATE);
					
					if(firstEntity.getRadius() <= 2)
						removeEntity(firstEntity);
				}
			}
		}
	}
	
	public void removeEntity(Entity entity)
	{
		entity.toBeDeleted(true);
	}
	
	public void queueEntityCreation(EntityType type, Entity parentEntity)
	{
		switch(type)
		{
			case CORE:
				EntityDefinition ed = new EntityDefinition();
				ed.type = EntityType.CORE;
				ed.color = parentEntity.getColor();
				
				if(parentEntity instanceof Giant)
					ed.radius = ((Giant)parentEntity).getOriginalRadius();
				else
					ed.radius = parentEntity.getRadius();
				
				ed.position = parentEntity.getPosition();
			
				queuedEntities.add(ed);
				break;
			case BLACKHOLE:
				EntityDefinition ed2 = new EntityDefinition();
				Giant giantEnt = (Giant) parentEntity;
				ed2.type = EntityType.BLACKHOLE;
				ed2.color = giantEnt.getColor();
				ed2.radius = giantEnt.getOriginalRadius()*0.7f;
				ed2.position = giantEnt.getPosition();
				ed2.velocity = giantEnt.getBody().getLinearVelocity();
				ed2.facingDirection = giantEnt.getBody().getAngle();
			
				queuedEntities.add(ed2);
				break;
			default:
				break;
		}
	}
	
	public void explodeEntity(Entity entity)
	{
		//createEntity(EntityType.CORE, entity.getColor(), entity.getRadius(), entity.getPosition().x, entity.getPosition().y, 0f, 0f);
		
		if(!(entity instanceof MassiveEntity))
		{
			queueEntityCreation(EntityType.CORE, entity);
		}
		
	/*	if(entity instanceof MassiveEntity)
		{
			if(((MassiveEntity) entity).canBlackHole())
				queueEntityCreation(EntityType.BLACKHOLE, entity);
			else
			{
				System.out.println("asd");
				queueEntityCreation(EntityType.CORE, entity);
			}
		}
		else
		{
			System.out.println("asd");
			queueEntityCreation(EntityType.CORE, entity);
		}*/
		
		entity.explode();
	}

	public void doGravity(Entity entity)
	{
		for(MassiveEntity gravEntity : gravityEntities)
		{
			if(entity != gravEntity)
			{
				//Gravity field is 10 times the radius of the body
				float distance = LGMath.distanceBetween(entity.getPosition(),gravEntity.getPosition());
				if(distance <= 10*gravEntity.getRadius())
				{
					Body entityBody = entity.getBody();
					
					float rotAngle = LGMath.getRotationTo(entity.getPosition(), gravEntity.getPosition());
					float forceMultiplier = (float)(gravEntity.getGravityMagnitude()*entity.getMass()*gravEntity.getRadius() * 1/Math.pow(distance,2));

					if(forceMultiplier != (Float.POSITIVE_INFINITY))
						entityBody.applyForceToCenter(new Vector2((float)(Math.cos(rotAngle) * forceMultiplier),(float)(Math.sin(rotAngle) * forceMultiplier)), true);
				}
				
				if(distance-entity.getRadius() <= 2.2*gravEntity.getRadius())
				{
					if(gravEntity instanceof BlackHole)
					{
						float rotAngle = LGMath.getRotationTo(entity.getPosition(), gravEntity.getPosition());
						
						long currentFireTime = System.currentTimeMillis();
						if(currentFireTime - entity.lastTimeFired > 200)
						{
							bulletHandler.createBulletsAndFire(gravEntity, entity.getRadius(), entity.getColor(), entity.getPosition().x, entity.getPosition().y, 500, rotAngle, 10);
							entity.addToRadius(-entity.getRadius()/20);
							entity.lastTimeFired = currentFireTime;
						}
					}
				}
			}
		}
	}
	
	public int getEntityNumber()
	{
		return entities.size();
	}
		
	public void update()
	{
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	Entity entity = entities.get(entityID);
	    	
	    	entity.update();
	    	
	    	//Gravity
	    	if(gravityEntities.size() > 0)
	    		doGravity(entity);
	    	
	    	//Update Box2D sizes
	    	if(entity.waitingToUpdateSize())
	    	{
	    		if(entity.getRadius() >= 2)
	    		{
	    			if(!world.isLocked())
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
	    		}
	    		else
	    		{
	    			removeEntity(entity);
	    		}
	    	}
	    	
	    	//Do explosion
	    	if(entity.getRadius() >= entity.getCritRadius())
	    	{
	    		explodeEntity(entity);
	    	}

	    	//Delete entities
	    	if(entity.waitingToBeDeleted())
	    	{
	    		if(!world.isLocked())
				{
		    		world.destroyBody(entity.getBody());
		    		entity.setRadius(0);
		    		entity.dispose();
		    		it.remove();
		    		
		    		if(gravityEntities.contains(entity))
		    			gravityEntities.remove(entity);
		    		
		    		entity = null;
				}
	    	}
	    }
	    
	    //Create the entities now
	    if(queuedEntities.size() > 0)
    	{
	    	if(!world.isLocked())
			{
	    		EntityDefinition ed = queuedEntities.remove();
	    		if(ed.type == EntityType.CORE)
	    			createEntity(EntityType.CORE, ed.color, ed.radius, 10, ed.position.x, ed.position.y, 0f, 0f);
	    		else if(ed.type == EntityType.BLACKHOLE)
	    			createEntity(EntityType.BLACKHOLE, ed.color, ed.radius, 20, ed.position.x, ed.position.y, ed.facingDirection, LGMath.getMagnitude(ed.velocity));
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
	
	public void dispose()
	{
		Iterator<Integer> it = entities.keySet().iterator();
		while (it.hasNext())
	    {
	    	entities.get((Integer)it.next()).dispose();
	    }
		
		entities = null;
		currentEntityID = 0;
	}
}

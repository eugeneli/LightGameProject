package com.eli.lightgame.entities;

import java.util.ArrayList;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.LightGameFilters;
import com.eli.lightgame.util.LGMath;

public class Player extends Entity
{
	private ParticleEffect particleEffect;
	
	private float rotateSpeed = 0.3f;
	private float targetRotAngle;
	private boolean rotateInstantly = true;
	private boolean needsToRotate = false;
	private boolean moveQueued = false;
	private boolean shotQueued = false;
	
	private Entity target;
	
	public Player(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float critRadius, float xPos, float yPos)
	{
		super("data/galaxy2.png", aColor, rad);	
		criticalRadius = critRadius;
		bulletHandler = bh;
		canChangeColor = true;
		canChangeSize = true;
		lightSize = 4.5f * radius;
		flickerRate = 0.5f;
		
		//Create player
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		circleDef.position.set(xPos, yPos);
		
		Body circleBody = world.createBody(circleDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(rad);
		
		FixtureDef circleFixture = new FixtureDef();
		circleFixture.shape = circleShape;
		circleFixture.density = 0.4f;
		circleFixture.friction = 1.0f;
		circleFixture.restitution = 1.0f;
		circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_ENTITY;
		circleFixture.filter.maskBits = LightGameFilters.MASK_ENTITY;
		
		circleBody.createFixture(circleFixture);
		
		//player lights
		ConeLight cl = new ConeLight(rayHandler, 50, color, lightSize, 0, 0, 0, 30);
		cl.attachToBody(circleBody, 2, 0);
		lights.add(cl);
		
		PointLight pl = new PointLight(rayHandler, 100, color, lightSize, 0, 0);
		PointLight pl2 = new PointLight(rayHandler, 50, Color.GRAY, radius, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		pl2.attachToBody(circleBody, 0, 0);
		lights.add(pl);
		lights.add(pl2);
		
		//Set the variables in Entity
		entityBody = circleBody;
		
		particleEffect = new ParticleEffect();
	    particleEffect.load(Gdx.files.internal("data/particleeffects/insides.p"), Gdx.files.internal("data"));
	    particleEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
	    particleEffect.start();
	    
	  /*  for(int i = 0; i < particleEffect.getEmitters().size; i++)
	    {
	    	particleEffect.getEmitters().get(i).
	    }*/
	}
	
	public void fire(int forceScalar)
	{
		bulletHandler.createBulletsAndFire(this, radius, color, getPosition().x, getPosition().y, forceScalar, entityBody.getAngle());
		
		addToRadius(-radius/10); //Decrease in radius equal to bullet's radius
	}
	
	public void fireTargeted(int forceScalar, Entity bulTarget)
	{
		bulletHandler.createBulletsAndFire(bulTarget, radius, color, getPosition().x, getPosition().y, forceScalar, entityBody.getAngle());
		
		addToRadius(-radius/10); //Decrease in radius equal to bullet's radius
	}
	
	public void turnToAngle(float rotAngle)
	{	
		needsToRotate = true;
		targetRotAngle = rotAngle;
	}
	
	public void turnAndShoot(float rotAngle)
	{
		turnToAngle(rotAngle);
		shotQueued = true;
	}
	
	public void turnAndShootTracking(float rotAngle, Entity bulTarget)
	{
		target = bulTarget;
		turnToAngle(rotAngle);
		shotQueued = true;
	}
	
	public void moveJoystick(float rotAngle)
	{
		if(rotAngle != 0)
		{
			entityBody.setAngularVelocity(0);
			float totalRotation = rotAngle - entityBody.getAngle();

			while ( totalRotation < Math.toRadians(-180)) totalRotation += Math.toRadians(360);
			while ( totalRotation > Math.toRadians(180)) totalRotation -= Math.toRadians(360);

			float change = 0.3f;
			float newAngle = entityBody.getAngle() + Math.min(change,  Math.max(-change,totalRotation));

			entityBody.setTransform(entityBody.getPosition(), newAngle);
			entityBody.applyForceToCenter(new Vector2((float)(Math.cos(entityBody.getAngle()) * (10*radius)),(float)(Math.sin(entityBody.getAngle()) * (10*radius))), true);
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(entityBody.getAngle()) * (30 + 1 * radius)),(float)(Math.sin(entityBody.getAngle()) * (30 + 1 * radius))));
		}
	}
	
	public void move(float rotAngle)
	{	
		if(!rotateInstantly)
		{
			turnToAngle(rotAngle);
			moveQueued = true;
		}
		else
		{
			entityBody.setAngularVelocity(0);
			entityBody.setTransform(entityBody.getPosition(), rotAngle);
			
		//	if(entityBody.getLinearVelocity().x/Math.cos(entityBody.getAngle()) < (30 + 1 * radius) && entityBody.getLinearVelocity().y/Math.sin(entityBody.getAngle()) < (30 + 1 * radius))
			//	entityBody.applyForceToCenter(new Vector2((float)(Math.cos(entityBody.getAngle()) * (5500*radius)),(float)(Math.sin(entityBody.getAngle()) * (5500*radius))), true);
			
		//	entityBody.applyForceToCenter(new Vector2((float)(Math.cos(entityBody.getAngle()) * (10*radius)),(float)(Math.sin(entityBody.getAngle()) * (10*radius))), true);
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(entityBody.getAngle()) * (30 + 1 * radius)),(float)(Math.sin(entityBody.getAngle()) * (30 + 1 * radius))));
		}
	}
	
	@Override
	public void updateSizes()
	{
		waitingToUpdateSize = true;
		
		size = 2 * radius;
		lightSize = 4 * radius;
		
		//increase the distance of the lights based on entity's size
		for(Light aLight : lights)
		{
			aLight.setDistance(lightSize);
		}
		
		//Increase texture size
		sprite.setSize(size, size);
	}
	
	public void update()
	{
		super.update();
		
		//Rotate to target if needed.
		float correctedEntAngle = LGMath.normalizeAndRoundAngle(entityBody.getAngle());
		float correctedTargetAngle = LGMath.normalizeAndRoundAngle(targetRotAngle);
		if(correctedEntAngle != correctedTargetAngle && needsToRotate)
		{
			entityBody.setAngularVelocity(0);
			float totalRotation = targetRotAngle - entityBody.getAngle();
			
			while ( totalRotation < Math.toRadians(-180)) totalRotation += Math.toRadians(360);
			while ( totalRotation > Math.toRadians(180)) totalRotation -= Math.toRadians(360);
			
			float newAngle = entityBody.getAngle() + Math.min(rotateSpeed,  Math.max(-rotateSpeed,totalRotation));
				
			entityBody.setTransform(entityBody.getPosition(), newAngle);
		}
		else if(moveQueued)
		{
			entityBody.applyForceToCenter(new Vector2((float)(Math.cos(entityBody.getAngle()) * (10*radius)),(float)(Math.sin(entityBody.getAngle()) * (10*radius))), true);
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(entityBody.getAngle()) * (40 + 1 * radius)),(float)(Math.sin(entityBody.getAngle()) * (40 + 1 * radius))));
			moveQueued = false;
		}
		else if(shotQueued)
		{
			//fire(500);
			
			fireTargeted(500,target);
			shotQueued = false;
			target = null;
		}
		
		if(correctedEntAngle == correctedTargetAngle)
			needsToRotate = false;
		
		//update particle position
		particleEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		particleEffect.update(Gdx.graphics.getDeltaTime());
		
		//flicker the core's light
		float currentCoreLightDistance = lights.get(2).getDistance();
		
		if(currentCoreLightDistance <= radius)
		{
			dimCoreLight = false;
		}
		else if(currentCoreLightDistance >= radius*1.5)
		{
			dimCoreLight = true;
		}
		
		if(dimCoreLight)
		{
			lights.get(2).setDistance(currentCoreLightDistance-flickerRate);
		}
		else
			lights.get(2).setDistance(currentCoreLightDistance+flickerRate);
	}
	
	public void draw(SpriteBatch batch)
	{
		super.draw(batch);
		particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
	}
}

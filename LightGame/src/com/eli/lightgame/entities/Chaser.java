package com.eli.lightgame.entities;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
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

public class Chaser extends NPC
{
	Bullet target = null;
	
	public Chaser(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos, float facingDirection, float velocity)
	{
		super("data/drifter.png", aColor, rad);
		
		bulletHandler = bh;
		criticalRadius = critSize;
		canChangeColor = true;
		canChangeSize = true;
		lightSize = 4.5f * radius;
		
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		circleDef.position.set(xPos, yPos);
		
		Body circleBody = world.createBody(circleDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(rad);
		
		FixtureDef circleFixture = new FixtureDef();
		circleFixture.shape = circleShape;
		circleFixture.density = 1.0f;
		circleFixture.friction = 1.0f;
		circleFixture.restitution = 1.0f;
		circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_ENTITY;
		circleFixture.filter.maskBits = LightGameFilters.MASK_ENTITY;
		
		circleBody.createFixture(circleFixture);
	
		//Lights
		ConeLight cl = new ConeLight(rayHandler, 50, color, lightSize, 0, 0, 0, 30);
		cl.attachToBody(circleBody, 2, 0);
		lights.add(cl);
		
		PointLight pl = new PointLight(rayHandler, 50, color, lightSize, 0, 0);
		PointLight pl2 = new PointLight(rayHandler, 50, Color.CYAN, radius, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		pl2.attachToBody(circleBody, 0, 0);
		lights.add(pl);
		lights.add(pl2);
		
		//Set the variables in Entity
		entityBody = circleBody;
	}
	
	public void updateSizes()
	{
		waitingToUpdateSize = true;
		
		size = 2 * radius;
		lightSize = 4.5f * radius;
		
		//increase the distance of the lights based on entity's size
		for(Light aLight : lights)
		{
			aLight.setDistance(lightSize);
		}

		//Increase texture size
		sprite.setSize(size, size);
	}
	
	public void move(float rotAngle)
	{	
		entityBody.setAngularVelocity(0);
		entityBody.setTransform(entityBody.getPosition(), rotAngle);
		//entityBody.applyForceToCenter(new Vector2((float)(Math.cos(entityBody.getAngle()) * (500*radius)),(float)(Math.sin(entityBody.getAngle()) * (500*radius))), true);
	//	entityBody.applyLinearImpulse(new Vector2((float)(Math.cos(entityBody.getAngle()) * (30 + 3f * radius)),(float)(Math.sin(entityBody.getAngle()) * (30 + 3f * radius))), entityBody.getWorldCenter(), true);
		//entityBody.
		entityBody.setLinearVelocity(new Vector2((float)(Math.cos(entityBody.getAngle()) * (30 + 1.5 * radius)),(float)(Math.sin(entityBody.getAngle()) * (30 + 1.5 * radius))));
	}
	
	public void update()
	{
		super.update();
		
		//flicker the core's light
		float currentCoreLightDistance = lights.get(1).getDistance();
		
		if(currentCoreLightDistance <= radius)
		{
			dimCoreLight = false;
		}
		else if(currentCoreLightDistance >= radius*4.5)
		{
			dimCoreLight = true;
		}
		
		if(dimCoreLight)
		{
			lights.get(1).setDistance(currentCoreLightDistance-flickerRate);
		}
		else
			lights.get(1).setDistance(currentCoreLightDistance+flickerRate);
	}
	
	public void toggleLights(boolean bool)
	{
		for(int i = 0; i < lights.size(); i++)
		{
			//lights.get(i).setActive(bool);
			if(bool)
				lights.get(i).setDistance(4.5f*radius);
			else
				lights.get(i).setDistance(radius*1.7f);
		}
	}
	
	

	@Override
	public void doAI()
	{
		target = bulletHandler.getSomeBullet(target);
		
		if(target != null)
		{
			toggleLights(true);
			move(LGMath.getRotationTo(getPosition(), target.getPosition()));
		}
		else
		{
			toggleLights(false);
		}
	}
}

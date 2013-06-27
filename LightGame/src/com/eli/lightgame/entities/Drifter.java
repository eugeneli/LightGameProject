package com.eli.lightgame.entities;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.LightGameFilters;

public class Drifter extends NPC
{
	private boolean alreadyMoving = false;
	private float originalDirection;
	private float originalVelocity;
	
	public Drifter(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos, float facingDirection, float velocity)
	{
		super("data/drifter.png", aColor, rad);
		
		bulletHandler = bh;
		criticalRadius = critSize;
		canChangeColor = true;
		canChangeSize = true;
		lightSize = 1.5f * radius;
		
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
		ArrayList<Light> drLights = new ArrayList<Light>();
		PointLight pl = new PointLight(rayHandler, 50, color, rad*10, 0, 0);
		//PointLight pl2 = new PointLight(rayHandler, 10, Color.CYAN, rad, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		//pl2.attachToBody(circleBody, 0, 0);
		drLights.add(pl);
		//drLights.add(pl2);
		
		//Set the variables in Entity
		entityBody = circleBody;
		lights = drLights;
		
		originalDirection = facingDirection;
		originalVelocity = velocity;
	}
	
	private void applyInitialVelocity()
	{
		if(!alreadyMoving)
		{
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(originalDirection) * originalVelocity),(float)(Math.sin(originalDirection) * originalVelocity)));
			alreadyMoving = true;
		}
	}
	
	public void updateSizes()
	{
		waitingToUpdateSize = true;
		
		size = 2 * radius;
		lightSize = 10 * radius;
		
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
		
		/*//flicker the core's light
		float currentCoreLightDistance = lights.get(1).getDistance();
		
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
			lights.get(1).setDistance(currentCoreLightDistance-flickerRate);
		}
		else
			lights.get(1).setDistance(currentCoreLightDistance+flickerRate);*/
	}

	@Override
	public void doAI() {
		// TODO Auto-generated method stub
		applyInitialVelocity();
	}
}
